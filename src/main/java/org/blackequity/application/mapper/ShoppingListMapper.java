package org.blackequity.application.mapper;


import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.shared.dto.ShoppingListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShoppingListMapper {

    List<ShoppingItem> toDto(List<ShoppingItem> items);

    default ShoppingListResponse toResponse(List<ShoppingItem> items) {
        List<ShoppingItem> dtos = toDto(items);
        return new ShoppingListResponse(dtos);
    }
}
