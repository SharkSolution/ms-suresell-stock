package org.blackequity.infrastructure.repository.panache.inventory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.blackequity.domain.model.inventory.InventoryConsumptionEntity;

@ApplicationScoped
public class InventoryConsumptionPanacheRepository implements PanacheRepository<InventoryConsumptionEntity> {
}
