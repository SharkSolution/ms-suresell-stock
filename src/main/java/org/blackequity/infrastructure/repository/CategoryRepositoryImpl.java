package org.blackequity.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.blackequity.domain.model.Category;
import org.blackequity.domain.repository.product.ICategoryRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepositoryImpl implements ICategoryRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public void save(Category category) {
        entityManager.persist(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Category.class, id));
    }

    @Override
    public List<Category> findAll() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }
}
