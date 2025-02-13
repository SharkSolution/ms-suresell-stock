package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.model.Product;
import org.blackequity.domain.repository.product.ICategoryRepository;
import org.blackequity.domain.repository.product.IProductRepository;
import org.blackequity.shared.dto.CreateProductDTO;

import java.util.List;

@ApplicationScoped
public class CreateProductUseCase {

    @Inject
    IProductRepository productRepository;

    @Inject
    ICategoryRepository categoryRepository;

    public void execute(CreateProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinStock(dto.getMinStock());
        product.setCategory(category);

        productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
