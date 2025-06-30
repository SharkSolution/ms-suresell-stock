package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.mapper.entity.ValeraEntityMapper;
import org.blackequity.domain.dto.Valera;
import org.blackequity.domain.enums.ValeraStatus;
import org.blackequity.domain.enums.ValeraType;
import org.blackequity.domain.model.ValeraEntity;
import org.blackequity.domain.repository.valera.ValeraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ValeraRepositoryImpl implements ValeraRepository, PanacheRepository<ValeraEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ValeraRepositoryImpl.class);

    @Inject
    ValeraEntityMapper mapper;

    @Override
    public List<Valera> findAllValeras() {
        logger.debug("Obteniendo todas las valeras");
        return listAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Valera> findById(String id) {
        logger.debug("Buscando valera por ID: {}", id);
        try{
            return find("code", id).stream()
                    .findFirst()
                    .map(mapper::toDomain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Valera> findByCode(String code) {
        logger.debug("Buscando valera por código: {}", code);
        return find("code", code).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public List<Valera> findByCustomerDocument(String customerDocument) {
        logger.debug("Buscando valeras del cliente: {}", customerDocument);
        return find("customerDocument", customerDocument).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByStatus(ValeraStatus status) {
        logger.debug("Buscando valeras con estado: {}", status);
        return find("status", status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByType(ValeraType type) {
        logger.debug("Buscando valeras de tipo: {}", type);
        return find("type", type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findActiveValeras() {
        logger.debug("Buscando valeras activas");
        return find("status = ?1 AND expirationDate >= ?2", ValeraStatus.ACTIVE, LocalDate.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findExpiringValeras(int daysAhead) {
        LocalDate limitDate = LocalDate.now().plusDays(daysAhead);
        logger.debug("Buscando valeras que vencen antes de: {}", limitDate);

        return find("status = ?1 AND expirationDate >= ?2 AND expirationDate <= ?3",
                ValeraStatus.ACTIVE, LocalDate.now(), limitDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findExpiredValeras() {
        logger.debug("Buscando valeras vencidas");
        return find("expirationDate < ?1 AND status != ?2", LocalDate.now(), ValeraStatus.EXPIRED)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando valeras en rango: {} - {}", startDate, endDate);
        return find("purchaseDate >= ?1 AND purchaseDate <= ?2 ORDER BY purchaseDate DESC",
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Valera create(Valera valera) {
        logger.debug("➕ Creando nueva valera: {}", valera.getCode());

        ValeraEntity newEntity = mapper.toEntity(valera);
        persist(newEntity);

        logger.info("✅ Valera creada: {} para cliente: {}", valera.getCode(), valera.getCustomerName());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public Valera update(Valera valera) {
        logger.debug("🔄 Actualizando valera: {}", valera.getCode());

        ValeraEntity existingEntity = find("id", valera.getId()).firstResult();
        if (existingEntity == null) {
            throw new IllegalArgumentException("Valera not found: " + valera.getId());
        }

        mapper.updateEntity(existingEntity, valera);
        logger.info("✅ Valera actualizada: {}", valera.getCode());
        return mapper.toDomain(existingEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        logger.debug("🗑️ Eliminando valera: {}", id);

        long deletedCount = delete("id", id);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("Valera not found: " + id);
        }

        logger.info("✅ Valera {} eliminada", id);
    }

    @Override
    public boolean existsByCode(String code) {
        return find("code", code).count() > 0;
    }

    @Override
    public long countByStatus(ValeraStatus status) {
        return find("status", status).count();
    }

    @Override
    public long countByCustomerDocument(String customerDocument) {
        return find("customerDocument", customerDocument).count();
    }
}
