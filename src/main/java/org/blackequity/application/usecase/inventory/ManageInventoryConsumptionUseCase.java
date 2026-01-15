package org.blackequity.application.usecase.inventory;

import org.blackequity.domain.dto.inventory.InventoryConsumption;

import java.util.List;

public interface ManageInventoryConsumptionUseCase {

    InventoryConsumption create(InventoryConsumption inventoryConsumption);

    List<InventoryConsumption> getAll();
}
