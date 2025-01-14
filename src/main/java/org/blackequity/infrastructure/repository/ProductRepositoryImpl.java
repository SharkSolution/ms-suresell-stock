package org.blackequity.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.domain.model.Product;
import org.blackequity.domain.repository.product.IProductRepository;
import org.blackequity.infrastructure.repository.panache.ProductPanacheRepository;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements IProductRepository {

    @Inject
    ProductPanacheRepository productPanacheRepository;

    @Override
    public void save(Product product) {
        productPanacheRepository.persist(product);
    }

    @Override
    public List<Product> findAll() {
        return productPanacheRepository.listAll();
    }

    @Override
    public List<Product> findByCategoryName(String categoryName) {
        return productPanacheRepository.findByCategoryName(categoryName);
    }
}