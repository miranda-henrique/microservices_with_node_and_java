package com.microservicesnodejava.productapi.modules.product.service;

import com.microservicesnodejava.productapi.config.SuccessResponse;
import com.microservicesnodejava.productapi.config.exception.ValidationException;
import com.microservicesnodejava.productapi.modules.category.service.CategoryService;
import com.microservicesnodejava.productapi.modules.product.dto.ProductQuantityDTO;
import com.microservicesnodejava.productapi.modules.product.dto.ProductRequest;
import com.microservicesnodejava.productapi.modules.product.dto.ProductResponse;
import com.microservicesnodejava.productapi.modules.product.dto.ProductStockDTO;
import com.microservicesnodejava.productapi.modules.product.model.Product;
import com.microservicesnodejava.productapi.modules.product.repository.ProductRepository;
import com.microservicesnodejava.productapi.modules.sales.dto.SalesConfirmationDTO;
import com.microservicesnodejava.productapi.modules.sales.enums.SalesStatus;
import com.microservicesnodejava.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import com.microservicesnodejava.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    public List<ProductResponse> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("Product ID must be informed");
        }

        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("No product for the provided id"));
    }

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("Product name must be informed");
        }

        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("Supplier ID must be informed");
        }

        return productRepository
                .findBySupplierId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("Category ID must be informed");
        }

        return productRepository
                .findByCategoryId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse save(ProductRequest request) {
        validateInformedProductData(request);
        validateInformedCategoryAndSupplier(request);

        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());

        var product = productRepository.save(Product.of(request, supplier, category));

        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        validateInformedProductData(request);
        validateInformedId(id);

        var supplier = supplierService.findById(request.getSupplierId());
        var category = categoryService.findById(request.getCategoryId());

        var product = Product.of(request, supplier, category);
        product.setId(id);

        productRepository.save(product);

        return ProductResponse.of(product);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        productRepository.deleteById(id);

        return SuccessResponse.create("Product deleted");
    }

    // UTILS

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("ID must be informed");
        }
    }

    private void validateInformedProductData(ProductRequest request) {
        if (isEmpty(request.getName())) {
            throw new ValidationException("Product name was not informed");
        }

        if (isEmpty(request.getAmountInStock())) {
            throw new ValidationException("Amount in stock was not informed");
        }

        if (request.getAmountInStock() <= ZERO) {
            throw new ValidationException("Amount in stock must not be less or equal to zero");
        }
    }

    private void validateInformedCategoryAndSupplier(ProductRequest request) {
        if (isEmpty(request.getCategoryId())) {
            throw new ValidationException("Category ID was not informed");
        }

        if (isEmpty(request.getSupplierId())) {
            throw new ValidationException("Supplier ID was not informed");
        }
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public void updateProductStock(ProductStockDTO product) {
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch (Exception e) {
            log.error("Error while trying to update stock for message with error: {}", e.getMessage(), e);
            var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED);
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    private void updateStock(ProductStockDTO product) {
        product
                .getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(Integer.valueOf(salesProduct.getProductId()));
                    validateAmountInStock(salesProduct, existingProduct);

                    existingProduct.updateStock(salesProduct.getQuantity());
                    productRepository.save(existingProduct);

                    var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED);
                    salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
                });
    }

    @Transactional
    private void validateStockUpdateData(ProductStockDTO product) {
        if (isEmpty(product) || isEmpty(product.getSalesId())) {
            throw new ValidationException("Product data or sales ID must be informed");
        }

        if (isEmpty(product.getProducts())) {
            throw new ValidationException("Sales' products must be informed");
        }

        product
                .getProducts()
                .forEach(salesProduct -> {
                    if (isEmpty(salesProduct.getQuantity()) || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("Product ID and quantity must be informed");
                    }
                });
    }

    private void validateAmountInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getAmountInStock()) {
            throw new ValidationException(
                    String.format("Product %s is out of stock",
                            existingProduct.getId())
            );
        }
    }
}
