package org.blackequity.infrastructure.repository;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateQuantityRequest {
    private BigDecimal quantity;
}