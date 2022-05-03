package com.microservicesnodejava.productapi.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservicesnodejava.productapi.modules.category.dto.CategoryResponse;
import com.microservicesnodejava.productapi.modules.product.model.Product;
import com.microservicesnodejava.productapi.modules.supplier.dto.SupplierResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Integer id;
    private String name;
    private SupplierResponse supplier;
    private CategoryResponse category;
    @JsonProperty("amount_in_stock")
    private Integer amountInStock;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    public static ProductResponse of(Product product) {
        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .amountInStock(product.getAmountInStock())
                .supplier(SupplierResponse.of(product.getSupplier()))
                .category(CategoryResponse.of(product.getCategory()))
                .createdAt(product.getCreatedAt())
                .build();
    }
}
