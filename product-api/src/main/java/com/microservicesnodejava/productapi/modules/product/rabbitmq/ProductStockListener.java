package com.microservicesnodejava.productapi.modules.product.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicesnodejava.productapi.modules.product.dto.ProductStockDTO;
import com.microservicesnodejava.productapi.modules.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductStockListener {

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "$ {app-config.rabbit.queue.product-stock}")
    public void receiveProductStockMessage(ProductStockDTO product) throws JsonProcessingException {
        log.info("Receiving message: {}", new ObjectMapper().writeValueAsString(product));
        productService.updateProductStock(product);
    }
}
