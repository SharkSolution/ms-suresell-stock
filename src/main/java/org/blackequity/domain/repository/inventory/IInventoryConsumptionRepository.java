package org.blackequity.domain.repository.inventory;

import org.blackequity.domain.model.inventory.InventoryConsumptionEntity;

import java.util.List;

public interface IInventoryConsumptionRepository {

    InventoryConsumptionEntity save(InventoryConsumptionEntity inventoryConsumptionEntity);

    List<InventoryConsumptionEntity> findAll();
}
