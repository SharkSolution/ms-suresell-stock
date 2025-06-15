package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.model.Product;
import org.blackequity.domain.repository.product.ICategoryRepository;
import org.blackequity.domain.repository.product.IProductRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GetProductsByCategoryUseCase {

    @Inject
    IProductRepository productRepository;

    @Inject
    ICategoryRepository categoryRepository;

    public List<Product> execute(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        String categoryName = category.get().getName();
        return productRepository.findByCategoryName(categoryName);
    }
}
