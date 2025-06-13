package org.blackequity.application.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.blackequity.domain.dto.ShoppingItem;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShoppingListMapper {

    public ShoppingListResponse toResponse(List<ShoppingItem> items) {
        if (items == null || items.isEmpty()) {
            return new ShoppingListResponse(List.of());
        }

        List<ShoppingItemDto> itemDtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new ShoppingListResponse(itemDtos);
    }

    public ShoppingItemDto toDto(ShoppingItem item) {
        if (item == null) return null;

        return new ShoppingItemDto(
                item.getId(),
                item.getProductId(),
                item.getName(),
                item.getCategory(),
                item.getUnit(),
                item.getCurrentStock(),
                item.getMinimumStock(),
                item.getSuggestedQuantity(),
                item.getEstimatedCost(),
                item.getStatus().toString(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    public List<ShoppingItemDto> toDtoList(List<ShoppingItem> items) {
        if (items == null) return List.of();

        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
