package org.blackequity.domain.repository.discount;

import org.blackequity.domain.model.DiscountUsageEntity;

import java.util.List;
import java.util.Optional;

public interface DiscountUsageRepository {
    DiscountUsageEntity save(DiscountUsageEntity usage);
    Optional<DiscountUsageEntity> findByOrderIdAndCouponId(Long orderId, Long couponId);
    List<DiscountUsageEntity> findByOrderId(Long orderId);
    List<DiscountUsageEntity> findByCouponId(Long couponId);
}
