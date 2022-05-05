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
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesResponse {

    private Integer id;
    private String name;
    @JsonProperty("amount_in_stock")
    private Integer amountInStock;
    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private SupplierResponse supplier;
    private CategoryResponse category;
    private List<String> sales;

    public static ProductSalesResponse of(Product product, List<String> sales) {
        return ProductSalesResponse
            .builder()
            .id(product.getId())
            .name(product.getName())
            .amountInStock(product.getAmountInStock())
            .createdAt(product.getCreatedAt())
            .supplier(SupplierResponse.of(product.getSupplier()))
            .category(CategoryResponse.of(product.getCategory()))
            .sales(sales)
            .build();
    }
}
