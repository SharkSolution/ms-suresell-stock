package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Product;
import org.blackequity.infrastructure.repository.ProductRepositoryImpl;
import org.blackequity.shared.dto.CreateProductDTO;

import java.util.List;

@ApplicationScoped
public class CreateProductUseCase {

    @Inject
    ProductRepositoryImpl productRepository;

    public void execute(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setMinStock(dto.getMinStock());
        // Aquí, en una versión futura, puedes mapear la categoría.

        productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
