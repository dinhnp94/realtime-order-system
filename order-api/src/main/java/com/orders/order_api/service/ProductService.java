package com.orders.order_api.service;

import com.orders.order_api.model.Product;
import com.orders.order_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProductService — demonstrates Redis caching in action.
 *
 * @Cacheable on getProductById():
 *   1st call → cache miss → fetches from PostgreSQL → stores in Redis with 5-min TTL
 *   2nd+ call → cache hit → returns from Redis directly (no DB query)
 *
 * Watch the logs when you call GET /products/prod-001 twice:
 *   1st call: "Cache MISS — fetching from PostgreSQL"
 *   2nd call: SILENCE (returned from Redis, method body never runs!)
 *
 * This is the cache-aside pattern — the most common caching strategy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(cacheNames = "products", key = "#id")
    // ^ Translates to: SET products::prod-001 <json> EX 300
    public Product getProductById(String id) {
        log.info("Cache MISS for product '{}' — fetching from PostgreSQL", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @CacheEvict(cacheNames = "products", key = "#product.id")
    // ^ Translates to: DEL products::prod-001
    public Product save(Product product) {
        log.info("Evicting product '{}' from Redis cache", product.getId());
        return productRepository.save(product);
    }
}
