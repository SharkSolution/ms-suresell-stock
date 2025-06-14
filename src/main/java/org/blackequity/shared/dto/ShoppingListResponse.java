package org.blackequity.shared.dto;

import lombok.Getter;
import lombok.Setter;
import org.blackequity.domain.dto.ShoppingItem;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ShoppingListResponse {
    private List<ShoppingItem> items;
    private int totalItems;
    private BigDecimal estimatedTotal;

    public ShoppingListResponse(List<ShoppingItem> items) {
        this.items = items;
        this.totalItems = items.size();
        this.estimatedTotal = items.stream()
                .map(ShoppingItem::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
