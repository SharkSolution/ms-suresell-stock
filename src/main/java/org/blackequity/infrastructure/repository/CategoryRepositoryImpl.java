package org.blackequity.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.repository.product.ICategoryRepository;
import org.blackequity.infrastructure.repository.panache.CategoryPanacheRepository;

import java.util.List;

@ApplicationScoped
public class CategoryRepositoryImpl implements ICategoryRepository {

    private final CategoryPanacheRepository categoryPanacheRepository;

    @Inject
    public CategoryRepositoryImpl(CategoryPanacheRepository categoryPanacheRepository) {
        this.categoryPanacheRepository = categoryPanacheRepository;
    }

    @Override
    public void save(Category category) {
        categoryPanacheRepository.persist(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryPanacheRepository.listAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryPanacheRepository.findById(id);
    }
}
