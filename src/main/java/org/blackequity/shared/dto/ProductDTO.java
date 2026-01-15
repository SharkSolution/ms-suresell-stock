package org.blackequity.shared.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal stock;
    private BigDecimal minStock;

    public ProductDTO(Long id, String name, BigDecimal price, BigDecimal stock, BigDecimal minStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }

}