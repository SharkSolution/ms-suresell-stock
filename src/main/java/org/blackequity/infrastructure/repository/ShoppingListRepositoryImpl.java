package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.enums.ShoppingItemStatus;
import org.blackequity.domain.model.ShoppingItemEntity;
import org.blackequity.domain.repository.shopping.ShoppingListRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShoppingListRepositoryImpl implements ShoppingListRepository, PanacheRepository<ShoppingItemEntity> {

    @Inject
    ShoppingItemEntityMapper mapper;

    @Override
    public List<ShoppingItem> findAll() {
        return listAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

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
        return findByIdOptional(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public ShoppingItem save(ShoppingItem item) {
        ShoppingItemEntity entity = mapper.toEntity(item);
        persist(entity);
        return mapper.toDomain(entity);
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
