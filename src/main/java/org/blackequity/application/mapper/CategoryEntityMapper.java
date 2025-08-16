package org.blackequity.application.mapper;

import org.blackequity.domain.dto.CategoryDto;
import org.blackequity.domain.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)

public interface CategoryEntityMapper {

    CategoryDto toDomain(Category entity);

    Category toEntity(CategoryDto domain);

    List<CategoryDto> toDomainList(List<Category> entities);

    List<Category> toEntityList(List<CategoryDto> domains);
}
