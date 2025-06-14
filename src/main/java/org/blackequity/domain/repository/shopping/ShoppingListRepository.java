package org.blackequity.domain.repository.shopping;

import org.blackequity.domain.dto.ShoppingItem;
import org.blackequity.domain.enums.ShoppingItemStatus;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository {
    List<ShoppingItem> findByStatus(ShoppingItemStatus status);
    List<ShoppingItem> findByCategory(String category);
    Optional<ShoppingItem> findById(String id);
    ShoppingItem save(ShoppingItem item);
    void deleteById(String id);
    List<ShoppingItem> findItemsNeedingRestock();
}
