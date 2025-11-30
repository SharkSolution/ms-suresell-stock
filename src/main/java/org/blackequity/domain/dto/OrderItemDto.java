package org.blackequity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private Long categoryId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
