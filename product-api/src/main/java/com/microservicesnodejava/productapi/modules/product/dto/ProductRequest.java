package com.microservicesnodejava.productapi.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {

    private String name;
    @JsonProperty("amount_in_stock")
    private Integer amountInStock;
    private Integer supplierId;
    private Integer categoryId;


}
