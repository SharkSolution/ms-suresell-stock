package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.mapper.ShoppingItemEntityMapper;
import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.enums.ShoppingItemStatus;
import org.blackequity.domain.model.ShoppingItemEntity;
import org.blackequity.domain.repository.shopping.ShoppingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShoppingListRepositoryImpl implements ShoppingListRepository, PanacheRepository<ShoppingItemEntity> {

    @Inject
    ShoppingItemEntityMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingListRepositoryImpl.class);



    @Override
    public List<ShoppingItem> findByStatus(ShoppingItemStatus status) {
        return find("status", status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingItem> findByCategory(String category) {
        return find("category", category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShoppingItem> findById(String id) {
        logger.debug("🔍 Buscando item por ID: {}", id);

        return find("id", id).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public ShoppingItem save(ShoppingItem item) {
        logger.debug(" Guardando item: {} (ID: {})", item.getName(), item.getId());

        if (item.getId() != null) {
            return updateExistingItem(item);
        } else {
            return createNewItem(item);
        }
    }

    private ShoppingItem updateExistingItem(ShoppingItem item) {
        logger.debug("Actualizando item existente con ID: {}", item.getId());

        ShoppingItemEntity existingEntity = find("id", item.getId()).firstResult();

        if (existingEntity == null) {
            logger.error("Item con ID {} no encontrado para actualizar", item.getId());
            throw new IllegalArgumentException("Item not found for update: " + item.getId());
        }

        updateEntityFields(existingEntity, item);


        logger.info("Item actualizado exitosamente: {}", item.getId());
        return mapper.toDomain(existingEntity);
    }
    private void updateEntityFields(ShoppingItemEntity entity, ShoppingItem domain) {
        logger.debug("🔧 Actualizando campos de entidad para ID: {}", domain.getId());

        // Actualizar TODOS los campos excepto ID y timestamps de creación
        entity.setProductId(domain.getProductId());
        entity.setName(domain.getName());
        entity.setCategory(domain.getCategory());
        entity.setUnit(domain.getUnit());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setMinimumStock(domain.getMinimumStock());
        entity.setSuggestedQuantity(domain.getSuggestedQuantity());
        entity.setEstimatedCost(domain.getEstimatedCost());
        entity.setStatus(domain.getStatus());
        entity.setUpdatedAt(domain.getUpdatedAt());
        // NO actualiza: id, createdAt
    }

    private ShoppingItem createNewItem(ShoppingItem item) {
        logger.debug("Creando nuevo item: {}", item.getName());

        ShoppingItemEntity newEntity = mapper.toEntity(item);
        persist(newEntity);

        logger.info("Nuevo item creado con ID: {}", newEntity.getId());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        delete("id", id);
    }

    @Override
    public List<ShoppingItem> findItemsNeedingRestock() {
        return find("currentStock <= minimumStock AND status = ?1", ShoppingItemStatus.PENDING)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
