package com.microservicesnodejava.productapi.modules.category.service;

import com.microservicesnodejava.productapi.config.SuccessResponse;
import com.microservicesnodejava.productapi.config.exception.ValidationException;
import com.microservicesnodejava.productapi.modules.category.dto.CategoryRequest;
import com.microservicesnodejava.productapi.modules.category.dto.CategoryResponse;
import com.microservicesnodejava.productapi.modules.category.model.Category;
import com.microservicesnodejava.productapi.modules.category.repository.CategoryRepository;
import com.microservicesnodejava.productapi.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findByDescription(String description) {
        if (isEmpty(description)) {
            throw new ValidationException("Category description must be informed");
        }

        return categoryRepository
                .findByDescriptionContainingIgnoreCase(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public Category findById(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("Id must be informed");
        }

        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("No category with given id"));
    }

    public CategoryResponse save(CategoryRequest request) {
        validateInformedCategoryName(request);
        var category = categoryRepository.save(Category.of(request));

        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request, Integer id) {
        validateInformedCategoryName(request);
        validateInformedId(id);

        var category = Category.of(request);
        category.setId(id);

        categoryRepository.save(category);

        return CategoryResponse.of(category);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("You cannot delete this category because it is linked to an existing product");
        }

        categoryRepository.deleteById(id);

        return SuccessResponse.create("Category deleted");
    }


    // VALIDATORS

    private void validateInformedCategoryName(CategoryRequest request) {
        if (isEmpty(request.getDescription())) {
            throw new ValidationException("Category description was not informed");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("ID must be informed");
        }
    }
}
