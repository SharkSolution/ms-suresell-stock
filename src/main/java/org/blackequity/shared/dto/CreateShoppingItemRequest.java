package org.blackequity.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateShoppingItemRequest {

    private String productId;
    private String name;
    private String category;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
}
