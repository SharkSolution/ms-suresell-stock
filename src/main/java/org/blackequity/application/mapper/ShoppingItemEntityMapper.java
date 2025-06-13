package org.blackequity.application.mapper;

import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.model.ShoppingItemEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = false)
)
public interface ShoppingItemEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "currentStock", source = "currentStock")
    @Mapping(target = "minimumStock", source = "minimumStock")
    @Mapping(target = "suggestedQuantity", source = "suggestedQuantity")
    @Mapping(target = "estimatedCost", source = "estimatedCost")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ShoppingItem toDomain(ShoppingItemEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "currentStock", source = "currentStock")
    @Mapping(target = "minimumStock", source = "minimumStock")
    @Mapping(target = "suggestedQuantity", source = "suggestedQuantity")
    @Mapping(target = "estimatedCost", source = "estimatedCost")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ShoppingItemEntity toEntity(ShoppingItem domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget ShoppingItemEntity entity, ShoppingItem domain);
}
