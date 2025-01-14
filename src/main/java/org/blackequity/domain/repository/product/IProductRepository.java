package org.blackequity.domain.repository.product;


import org.blackequity.domain.model.Product;

import java.util.List;

public interface IProductRepository {
    void save(Product product);
    List<Product> findAll();
    List<Product> findByCategoryName(String categoryName);
}