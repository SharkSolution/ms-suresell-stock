package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.mapper.ShoppingListMapper;
import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.enums.ShoppingItemStatus;
import org.blackequity.domain.repository.shopping.ShoppingListRepository;
import org.blackequity.shared.dto.CreateShoppingItemRequest;
import org.blackequity.shared.dto.ShoppingListResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ManageShoppingListUseCase {

    @Inject
    ShoppingListRepository repository;

    @Inject
    ShoppingListMapper mapper;

    public ShoppingListResponse getActiveShoppingList() {
        List<ShoppingItem> itemsPending = repository.findByStatus(ShoppingItemStatus.PENDING);
        List<ShoppingItem> itemsPurchase = repository.findByStatus(ShoppingItemStatus.PURCHASED);

        List<ShoppingItem> itemsConcated = new ArrayList<>();
        itemsConcated.addAll(itemsPurchase);
        itemsConcated.addAll(itemsPending);
        return mapper.toResponse(itemsConcated);
    }

    public ShoppingItem addItem(CreateShoppingItemRequest request) throws Exception {
        ShoppingItem item = new ShoppingItem(
                request.getProductId(),
                request.getName(),
                request.getCategory(),
                request.getUnit(),
                request.getCurrentStock(),
                request.getMinimumStock()
        );
        try{
            return repository.save(item);
        }catch (Exception e) {
            throw new Exception("www");
        }
    }

    public void updateItemQuantity(String itemId, BigDecimal quantity) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.updateQuantity(quantity);
        try{
            repository.save(item);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
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