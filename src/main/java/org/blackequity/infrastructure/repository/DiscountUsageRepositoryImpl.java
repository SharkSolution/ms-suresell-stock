package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.blackequity.domain.model.DiscountUsageEntity;
import org.blackequity.domain.repository.discount.DiscountUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DiscountUsageRepositoryImpl implements DiscountUsageRepository, PanacheRepository<DiscountUsageEntity> {

    private static final Logger logger = LoggerFactory.getLogger(DiscountUsageRepositoryImpl.class);

    @Override
    @Transactional
    public DiscountUsageEntity save(DiscountUsageEntity usage) {
        logger.debug("Guardando uso de cupón para orden: {}", usage.getOrderId());
        if (usage.getId() == null) {
            persist(usage);
        } else {
            usage = getEntityManager().merge(usage);
        }
        return usage;
    }

    @Override
    public Optional<DiscountUsageEntity> findByOrderIdAndCouponId(Long orderId, Long couponId) {
        logger.debug("Buscando uso de cupón para orden {} y cupón {}", orderId, couponId);
        return find("orderId = ?1 and coupon.id = ?2", orderId, couponId).firstResultOptional();
    }

    @Override
    public List<DiscountUsageEntity> findByOrderId(Long orderId) {
        logger.debug("Buscando usos de cupones para orden: {}", orderId);
        return find("orderId", orderId).list();
    }

    @Override
    public List<DiscountUsageEntity> findByCouponId(Long couponId) {
        logger.debug("Buscando usos del cupón: {}", couponId);
        return find("coupon.id", couponId).list();
    }
}
