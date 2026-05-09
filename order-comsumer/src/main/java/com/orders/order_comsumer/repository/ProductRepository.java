package com.orders.order_comsumer.repository;

import com.orders.order_comsumer.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Atomic stock decrement — runs as a single SQL UPDATE.
     *
     * Why not: find product → subtract → save?
     * Because two consumers could run that simultaneously:
     *   Consumer A reads stock=10, Consumer B reads stock=10
     *   Both subtract 1, both write stock=9 → actual stock should be 8! (race condition)
     *
     * This single UPDATE is atomic — only one thread can execute it at a time.
     * The WHERE stock >= quantity clause also prevents going negative.
     *
     * Returns: 1 if update succeeded, 0 if stock was insufficient
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    int decrementStock(String productId, int quantity);
}
