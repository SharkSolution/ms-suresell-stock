package org.blackequity.domain.repository.discount;

import org.blackequity.domain.model.DiscountCouponEntity;

import java.util.List;
import java.util.Optional;

public interface DiscountCouponRepository {
    Optional<DiscountCouponEntity> findByCode(String code);
    Optional<DiscountCouponEntity> findByCodeIgnoreCase(String code);
    Optional<DiscountCouponEntity> findCouponById(Long id);
    List<DiscountCouponEntity> findAllActive();
    List<DiscountCouponEntity> findAllCoupons();
    List<DiscountCouponEntity> findByActiveStatus(Boolean isActive);
    DiscountCouponEntity save(DiscountCouponEntity coupon);
    boolean existsByCode(String code);
    void deleteCoupon(DiscountCouponEntity coupon);
}
