package org.blackequity.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount_usages",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_coupon", columnNames = {"order_id", "coupon_id"})
        },
        indexes = {
                @Index(name = "idx_usage_order", columnList = "order_id"),
                @Index(name = "idx_usage_coupon", columnList = "coupon_id"),
                @Index(name = "idx_usage_created", columnList = "created_at")
        })
public class DiscountUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private DiscountCouponEntity coupon;

    @Column(name = "discount_code", nullable = false, length = 50)
    private String discountCode;

    @Column(name = "subtotal_before_discount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalBeforeDiscount;

    @Column(name = "discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "total_after_discount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAfterDiscount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
