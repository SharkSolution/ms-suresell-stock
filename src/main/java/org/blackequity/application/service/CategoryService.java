package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.usecase.CreateCategoryUseCase;
import org.blackequity.shared.dto.CreateCategoryDTO;

@ApplicationScoped
public class CategoryService {

    @Inject
    CreateCategoryUseCase createCategoryUseCase;

    @Transactional
    public void createCategory(CreateCategoryDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        createCategoryUseCase.execute(dto);
    }
}