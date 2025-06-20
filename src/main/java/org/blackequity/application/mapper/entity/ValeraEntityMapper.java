package org.blackequity.application.mapper.entity;

import org.blackequity.domain.dto.Valera;
import org.blackequity.domain.model.ValeraEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ValeraEntityMapper {

    Valera toDomain(ValeraEntity entity);

    ValeraEntity toEntity(Valera domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget ValeraEntity entity, Valera domain);
}
