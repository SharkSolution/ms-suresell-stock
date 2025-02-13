package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.repository.product.ICategoryRepository;
import org.blackequity.shared.dto.CreateCategoryDTO;

@ApplicationScoped
public class CreateCategoryUseCase {

    @Inject
    ICategoryRepository categoryRepository;

    public void execute(CreateCategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        categoryRepository.save(category);
    }
}
