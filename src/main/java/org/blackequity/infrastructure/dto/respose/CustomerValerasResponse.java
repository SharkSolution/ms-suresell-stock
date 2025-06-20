package org.blackequity.infrastructure.dto.respose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blackequity.shared.dto.ValeraDto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerValerasResponse {
    private String customerDocument;
    private String customerName;
    private List<ValeraDto> valeras;
    private int totalValeras;
    private int activeValeras;
    private BigDecimal totalSpent;
    private BigDecimal totalSavings;
}
