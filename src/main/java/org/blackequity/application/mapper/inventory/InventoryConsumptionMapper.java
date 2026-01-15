package org.blackequity.application.mapper.inventory;

import org.blackequity.domain.dto.inventory.InventoryConsumption;
import org.blackequity.domain.model.inventory.InventoryConsumptionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface InventoryConsumptionMapper {

    InventoryConsumption toDto(InventoryConsumptionEntity entity);

    InventoryConsumptionEntity toEntity(InventoryConsumption dto);

    List<InventoryConsumption> toDtoList(List<InventoryConsumptionEntity> entityList);
}
