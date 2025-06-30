package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.usecase.ManageShoppingListUseCase;
import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.shared.dto.CreateShoppingItemRequest;
import org.blackequity.shared.dto.ShoppingListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ShoppingListService {

    @Inject
    ManageShoppingListUseCase useCase;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingListService.class);


    public ShoppingListResponse getCurrentList() {
        ShoppingListResponse lista = useCase.getActiveShoppingList();
        return lista;
    }

    public ShoppingItem createItem(CreateShoppingItemRequest request) throws Exception {
        validateRequest(request);
        return useCase.addItem(request);
    }

    public void updateQuantity(String itemId, BigDecimal quantity) {
        validateQuantity(quantity);
        logger.debug("testttttt: {}", quantity);

        useCase.updateItemQuantity(itemId, quantity);
    }

    public void purchaseItem(String itemId) {
        useCase.markItemAsPurchased(itemId);
    }

    public List<ShoppingItem> generateAutomaticShoppingList() {
        return useCase.generateAutomaticList();
    }

    public void deleteItem(String itemId) {
        useCase.removeItem(itemId);
    }

    private void validateRequest(CreateShoppingItemRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (request.getCurrentStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }
    }

    private void validateQuantity(BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
