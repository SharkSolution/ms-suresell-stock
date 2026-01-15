package org.blackequity.infrastructure.repository.inventory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.domain.model.inventory.InventoryConsumptionEntity;
import org.blackequity.domain.repository.inventory.IInventoryConsumptionRepository;
import org.blackequity.infrastructure.repository.panache.inventory.InventoryConsumptionPanacheRepository;

import java.util.List;

@ApplicationScoped
public class InventoryConsumptionRepositoryImpl implements IInventoryConsumptionRepository {

    @Inject
    InventoryConsumptionPanacheRepository panacheRepository;

    @Override
    @Transactional
    public InventoryConsumptionEntity save(InventoryConsumptionEntity inventoryConsumptionEntity) {
        panacheRepository.persist(inventoryConsumptionEntity);
        return inventoryConsumptionEntity;
    }

    @Override
    public List<InventoryConsumptionEntity> findAll() {
        return panacheRepository.listAll();
    }
}
