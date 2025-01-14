package org.blackequity.application.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.usecase.CreateProductUseCase;
import org.blackequity.shared.dto.CreateProductDTO;
import org.blackequity.shared.dto.ProductDTO;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    CreateProductUseCase createProductUseCase;

    public void createProduct(CreateProductDTO dto) {
        createProductUseCase.execute(dto);
    }

    public List<ProductDTO> getAllProducts() {
        return createProductUseCase.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getMinStock()))
                .collect(Collectors.toList());
    }
}
