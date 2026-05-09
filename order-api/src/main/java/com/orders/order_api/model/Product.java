package com.orders.order_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * Product — maps to the "products" table in PostgreSQL.
 *
 * Implements Serializable because Redis needs to serialize this object
 * into bytes when storing it. Without this, @Cacheable would crash.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements Serializable {

    @Id
    private String id;                    // e.g. "prod-001" (we set this manually, not auto-generate)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String description;
    private String category;
}
