package com.microservicesnodejava.productapi.modules.product.model;

import com.microservicesnodejava.productapi.modules.category.model.Category;
import com.microservicesnodejava.productapi.modules.product.dto.ProductRequest;
import com.microservicesnodejava.productapi.modules.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FK_CATEGORY", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "FK_SUPPLIER", nullable = false)
    private Supplier supplier;

    @Column(name = "AMOUNT_IN_STOCK", nullable = false)
    private Integer amountInStock;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static Product of(ProductRequest request, Supplier supplier, Category category) {
        return Product
                .builder()
                .name(request.getName())
                .amountInStock(request.getAmountInStock())
                .supplier(supplier)
                .category(category)
                .build();
    }

}
