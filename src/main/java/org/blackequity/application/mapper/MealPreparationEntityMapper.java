package org.blackequity.application.mapper;

import org.blackequity.domain.dto.MealPreparation;
import org.blackequity.domain.model.MealPreparationEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MealPreparationEntityMapper {

    MealPreparation toDomain(MealPreparationEntity entity);

    MealPreparationEntity toEntity(MealPreparation domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget MealPreparationEntity entity, MealPreparation domain);
}
