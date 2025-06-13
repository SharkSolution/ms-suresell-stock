package org.blackequity.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.blackequity.domain.enums.ShoppingItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shopping_items")
public class ShoppingItemEntity {
    @Id
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "unit")
    private String unit;

    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "minimum_stock", precision = 10, scale = 2)
    private BigDecimal minimumStock;

    @Column(name = "suggested_quantity", precision = 10, scale = 2)
    private BigDecimal suggestedQuantity;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShoppingItemStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor vacío para JPA
    public ShoppingItemEntity() {}

    // Constructor completo
    public ShoppingItemEntity(String id, String productId, String name, String category,
                              String unit, BigDecimal currentStock, BigDecimal minimumStock,
                              BigDecimal suggestedQuantity, BigDecimal estimatedCost,
                              ShoppingItemStatus status, LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.suggestedQuantity = suggestedQuantity;
        this.estimatedCost = estimatedCost;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
