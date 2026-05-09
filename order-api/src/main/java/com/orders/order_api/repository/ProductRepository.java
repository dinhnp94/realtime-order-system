package com.orders.order_api.repository;

import com.orders.order_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository — your database access layer for products.
 *
 * JpaRepository gives you these for FREE (no code needed):
 *   findById(id)     → SELECT * FROM products WHERE id = ?
 *   findAll()        → SELECT * FROM products
 *   save(product)    → INSERT or UPDATE
 *   deleteById(id)   → DELETE FROM products WHERE id = ?
 *
 * Spring generates the actual SQL at runtime. You never write SQL for basic operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // JpaRepository<Product, String> means: entity=Product, primary key type=String
}
