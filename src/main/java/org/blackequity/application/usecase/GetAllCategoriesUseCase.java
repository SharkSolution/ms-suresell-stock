package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.repository.product.ICategoryRepository;

import java.util.List;

@ApplicationScoped
public class GetAllCategoriesUseCase {

    @Inject
    ICategoryRepository categoryRepository;

    public List<Category> execute() {
        return categoryRepository.findAll();
    }
}
