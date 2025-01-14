package org.blackequity.domain.repository.product;

import org.blackequity.domain.model.Category;

import java.util.List;

public interface ICategoryRepository {
    void save(Category category);
    List<Category> findAll();
    Category findById(Long id);
}

