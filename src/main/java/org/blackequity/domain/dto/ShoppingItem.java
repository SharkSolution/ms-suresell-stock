package org.blackequity.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blackequity.domain.enums.ShoppingItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ShoppingItem {
    private String id;
    private String productId;
    private String name;
    private String category;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private BigDecimal suggestedQuantity;
    private BigDecimal estimatedCost;
    private ShoppingItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShoppingItem(String productId, String name, String category, String unit,
                        BigDecimal currentStock, BigDecimal minimumStock) {
        this.id = UUID.randomUUID().toString();
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.suggestedQuantity = calculateSuggestedQuantity();
        this.estimatedCost = BigDecimal.ZERO;
        this.status = ShoppingItemStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private BigDecimal calculateSuggestedQuantity() {
        if (minimumStock == null || currentStock == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal difference = minimumStock.multiply(BigDecimal.valueOf(2)).subtract(currentStock);
        return difference.compareTo(BigDecimal.ZERO) > 0 ? difference : BigDecimal.ZERO;
    }

    public void updateQuantity(BigDecimal quantity) {
        this.suggestedQuantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateEstimatedCost(BigDecimal cost) {
        this.estimatedCost = cost;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPurchased() {
        this.status = ShoppingItemStatus.PURCHASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ShoppingItemStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean needsRestocking() {
        return currentStock.compareTo(minimumStock) <= 0;
    }
}

