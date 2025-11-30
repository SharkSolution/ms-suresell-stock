package org.blackequity.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blackequity.domain.enums.AppliesToType;
import org.blackequity.domain.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount_coupons",
        indexes = {
                @Index(name = "idx_discount_code", columnList = "code", unique = true),
                @Index(name = "idx_discount_active", columnList = "is_active"),
                @Index(name = "idx_discount_valid_dates", columnList = "valid_from,valid_to")
        })
public class DiscountCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to_type", nullable = false, length = 20)
    private AppliesToType appliesToType;

    @Column(name = "applies_to_id")
    private Long appliesToId;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "valid_weekdays", length = 50)
    private String validWeekdays;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
