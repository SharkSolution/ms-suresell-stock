package org.blackequity.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductDTO {
    private String name;
    private BigDecimal price;
    private BigDecimal stock;
    private BigDecimal minStock;
    private Long categoryId;

}