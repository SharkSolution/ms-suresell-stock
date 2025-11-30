package org.blackequity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blackequity.domain.enums.DiscountType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDiscountResult {
    private Boolean valid;
    private String discountCode;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal newSubtotal;
    private String message;
}
