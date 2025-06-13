package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.enums.ShoppingItemStatus;
import org.blackequity.domain.model.ShoppingItemEntity;
import org.blackequity.domain.repository.shopping.ShoppingListRepository;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ManageShoppingListUseCase {

    @Inject
    ShoppingListRepository repository;

    @Inject
    ShoppingListMapper mapper;

    public ShoppingListResponse getActiveShoppingList() {
        List<ShoppingItem> items = repository.findByStatus(ShoppingItemStatus.PENDING);
        return mapper.toResponse(items);
    }

    public ShoppingItem addItem(CreateShoppingItemRequest request) {
        ShoppingItem item = new ShoppingItem(
                request.getProductId(),
                request.getName(),
                request.getCategory(),
                request.getUnit(),
                request.getCurrentStock(),
                request.getMinimumStock()
        );

        return repository.save(item);
    }

    public void updateItemQuantity(String itemId, BigDecimal quantity) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.updateQuantity(quantity);
        repository.save(item);
    }

    public void markItemAsPurchased(String itemId) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.markAsPurchased();
        repository.save(item);
    }

    public List<ShoppingItem> generateAutomaticList() {
        return repository.findItemsNeedingRestock();
    }

    public void removeItem(String itemId) {
        repository.deleteById(itemId);
    }
}