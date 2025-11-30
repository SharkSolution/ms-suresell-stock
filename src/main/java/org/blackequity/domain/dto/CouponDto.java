package org.blackequity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blackequity.domain.enums.AppliesToType;
import org.blackequity.domain.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private AppliesToType appliesToType;
    private Long appliesToId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String validWeekdays;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
