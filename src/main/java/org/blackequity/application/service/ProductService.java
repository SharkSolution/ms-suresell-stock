package org.blackequity.application.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.usecase.CreateProductUseCase;
import org.blackequity.application.usecase.GetProductsByCategoryUseCase;
import org.blackequity.domain.model.Product;
import org.blackequity.shared.dto.CreateProductDTO;
import org.blackequity.shared.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    CreateProductUseCase createProductUseCase;

    @Inject
    GetProductsByCategoryUseCase getProductsByCategoryUseCase;

    @Transactional
    public void createProduct(CreateProductDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        createProductUseCase.execute(dto);
    }

    public List<ProductDTO> getAllProducts() {
        return createProductUseCase.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getMinStock()))
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        List<Product> products = getProductsByCategoryUseCase.execute(categoryId);

        return products.stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getMinStock()))
                .collect(Collectors.toList());
    }
}
