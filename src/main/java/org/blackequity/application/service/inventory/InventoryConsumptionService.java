package org.blackequity.application.service.inventory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.mapper.inventory.InventoryConsumptionMapper;
import org.blackequity.application.usecase.inventory.ManageInventoryConsumptionUseCase;
import org.blackequity.domain.dto.inventory.InventoryConsumption;
import org.blackequity.domain.model.inventory.InventoryConsumptionEntity;
import org.blackequity.domain.repository.inventory.IInventoryConsumptionRepository;
import org.blackequity.domain.repository.product.IProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class InventoryConsumptionService implements ManageInventoryConsumptionUseCase {

    @Inject
    IInventoryConsumptionRepository inventoryConsumptionRepository;

    @Inject
    IProductRepository productRepository;

    @Inject
    InventoryConsumptionMapper mapper;

    @Override
    public InventoryConsumption create(InventoryConsumption inventoryConsumption) {
        inventoryConsumption.setRegistrationDate(LocalDateTime.now());
        InventoryConsumptionEntity entity = mapper.toEntity(inventoryConsumption);
        inventoryConsumptionRepository.save(entity);
        productRepository.updateStock(entity.getProductId(), entity.getQuantity().negate());
        return mapper.toDto(entity);
    }

    @Override
    public List<InventoryConsumption> getAll() {
        return mapper.toDtoList(inventoryConsumptionRepository.findAll());
    }
}
