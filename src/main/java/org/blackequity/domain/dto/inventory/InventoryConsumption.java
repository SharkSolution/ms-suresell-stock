package org.blackequity.domain.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.blackequity.domain.inventory.enums.Reason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryConsumption {

    private Long id;
    private Long productId;
    private BigDecimal quantity;
    private Reason reason;
    private LocalDateTime registrationDate;
    private String registeredBy;
    private String observations;
}
