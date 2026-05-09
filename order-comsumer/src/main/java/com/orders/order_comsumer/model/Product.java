package com.orders.order_comsumer.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Product — maps to the same "products" table created by order-api.
 * order-consumer only needs to UPDATE stock, so we map to the same table.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String description;
    private String category;
}
