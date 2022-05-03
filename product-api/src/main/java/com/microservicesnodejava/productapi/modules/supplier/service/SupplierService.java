package com.microservicesnodejava.productapi.modules.supplier.service;

import com.microservicesnodejava.productapi.config.SuccessResponse;
import com.microservicesnodejava.productapi.config.exception.ValidationException;
import com.microservicesnodejava.productapi.modules.product.service.ProductService;
import com.microservicesnodejava.productapi.modules.supplier.dto.SupplierRequest;
import com.microservicesnodejava.productapi.modules.supplier.dto.SupplierResponse;
import com.microservicesnodejava.productapi.modules.supplier.model.Supplier;
import com.microservicesnodejava.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;

    public List<SupplierResponse> findAll() {
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }

    public Supplier findById(Integer id) {
        validateInformedId(id);

        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("No supplier for the given id"));
    }

    public List<SupplierResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("Supplier name must be informed");
        }

        return supplierRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse save(SupplierRequest request) {
        validateInformedSupplierName(request);
        var supplier = supplierRepository.save(Supplier.of(request));

        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id) {
        validateInformedSupplierName(request);
        validateInformedId(id);

        var supplier = Supplier.of(request);
        supplier.setId(id);

        supplierRepository.save(supplier);

        supplierRepository.save(supplier);

        return SupplierResponse.of(supplier);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        if (productService.existsBySupplierId(id)) {
            throw new ValidationException("You cannot delete this supplier because it is linked to an existing product");
        }

        supplierRepository.deleteById(id);

        return SuccessResponse.create("Supplier deleted");
    }

    // UTILS

    private void validateInformedSupplierName(SupplierRequest request) {
        if (isEmpty(request.getName())) {
            throw new ValidationException("Supplier's name was not informed");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("ID must be informed");
        }
    }

}
