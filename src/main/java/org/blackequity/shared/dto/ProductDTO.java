package org.blackequity.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;

    public ProductDTO(Long id, String name, BigDecimal price, Integer stock, Integer minStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }

}