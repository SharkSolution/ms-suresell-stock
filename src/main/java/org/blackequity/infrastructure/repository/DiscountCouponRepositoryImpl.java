package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.blackequity.domain.model.DiscountCouponEntity;
import org.blackequity.domain.repository.discount.DiscountCouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DiscountCouponRepositoryImpl implements DiscountCouponRepository, PanacheRepository<DiscountCouponEntity> {

    private static final Logger logger = LoggerFactory.getLogger(DiscountCouponRepositoryImpl.class);

    @Override
    public Optional<DiscountCouponEntity> findByCode(String code) {
        logger.debug("Buscando cupón por código: {}", code);
        return find("code", code).firstResultOptional();
    }

    @Override
    public Optional<DiscountCouponEntity> findByCodeIgnoreCase(String code) {
        logger.debug("Buscando cupón por código (case insensitive): {}", code);
        return find("UPPER(code) = UPPER(?1)", code).firstResultOptional();
    }

    @Override
    public List<DiscountCouponEntity> findAllActive() {
        logger.debug("Obteniendo todos los cupones activos");
        return find("isActive", true).list();
    }

    @Override
    @Transactional
    public DiscountCouponEntity save(DiscountCouponEntity coupon) {
        logger.debug("Guardando cupón: {}", coupon.getCode());
        if (coupon.getId() == null) {
            persist(coupon);
        } else {
            coupon = getEntityManager().merge(coupon);
        }
        return coupon;
    }

    @Override
    public boolean existsByCode(String code) {
        logger.debug("Verificando existencia de cupón: {}", code);
        return count("code", code) > 0;
    }

    @Override
    public Optional<DiscountCouponEntity> findCouponById(Long id) {
        logger.debug("Buscando cupón por ID: {}", id);
        return findByIdOptional(id);
    }

    @Override
    public List<DiscountCouponEntity> findAllCoupons() {
        logger.debug("Obteniendo todos los cupones");
        return listAll();
    }

    @Override
    public List<DiscountCouponEntity> findByActiveStatus(Boolean isActive) {
        logger.debug("Obteniendo cupones con estado activo: {}", isActive);
        return find("isActive", isActive).list();
    }

    @Override
    @Transactional
    public void deleteCoupon(DiscountCouponEntity coupon) {
        logger.debug("Eliminando cupón: {}", coupon.getCode());
        delete(coupon);
    }
}
