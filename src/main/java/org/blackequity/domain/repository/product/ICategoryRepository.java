package org.blackequity.domain.repository.product;

import org.blackequity.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository {
    void save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
}

