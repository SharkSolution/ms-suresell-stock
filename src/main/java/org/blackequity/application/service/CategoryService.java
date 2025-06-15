package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.mapper.CategoryEntityMapper;
import org.blackequity.application.usecase.CreateCategoryUseCase;
import org.blackequity.application.usecase.GetAllCategoriesUseCase;
import org.blackequity.domain.dto.CategoryDto;
import org.blackequity.domain.model.Category;
import org.blackequity.shared.dto.CreateCategoryDTO;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

    @Inject
    CreateCategoryUseCase createCategoryUseCase;

    @Inject
    GetAllCategoriesUseCase getAllCategoriesUseCase;

    @Inject
    CategoryEntityMapper categoryMapper;

    @Transactional
    public void createCategory(CreateCategoryDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        createCategoryUseCase.execute(dto);
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = getAllCategoriesUseCase.execute();
        return categoryMapper.toDomainList(categories);
    }
}