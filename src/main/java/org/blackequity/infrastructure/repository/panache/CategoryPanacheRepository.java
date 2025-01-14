package org.blackequity.infrastructure.repository.panache;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.blackequity.domain.model.Category;

@ApplicationScoped
public class CategoryPanacheRepository implements PanacheRepository<Category> {
}
