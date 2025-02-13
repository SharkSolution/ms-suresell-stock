package org.blackequity.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.blackequity.domain.model.Product;
import org.blackequity.domain.repository.product.IProductRepository;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements IProductRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public void save(Product product) {
        entityManager.persist(product);
    }

    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    @Override
    public List<Product> findByCategoryName(String categoryName) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.category.name = :categoryName", Product.class)
                .setParameter("categoryName", categoryName)
                .getResultList();
    }
}