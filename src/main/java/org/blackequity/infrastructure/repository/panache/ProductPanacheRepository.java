package org.blackequity.infrastructure.repository.panache;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.blackequity.domain.model.Product;

import java.util.List;

@ApplicationScoped
public class ProductPanacheRepository implements PanacheRepository<Product> {

    public List<Product> findByCategoryName(String categoryName) {
        return find("category.name", categoryName).list();
    }
}