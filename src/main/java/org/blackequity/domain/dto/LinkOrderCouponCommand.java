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
public class LinkOrderCouponCommand {
    private Long orderId;
    private String code;
    private BigDecimal subtotalBeforeDiscount;
    private BigDecimal discountAmount;
    private BigDecimal totalAfterDiscount;
}
