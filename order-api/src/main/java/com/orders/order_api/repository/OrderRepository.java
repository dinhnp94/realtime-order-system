package com.orders.order_api.repository;

import com.orders.order_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderRepository — database access for orders.
 *
 * Spring reads the method name "findByUserId" and automatically generates:
 *   SELECT * FROM orders WHERE user_id = ?
 *
 * This is called "derived queries" — you name the method, Spring writes the SQL.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
}
