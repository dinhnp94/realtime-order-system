package com.orders.order_api.controller;

import com.orders.order_api.model.Product;
import com.orders.order_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** GET /products — all products from DB (no cache) */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /products/{id} — single product.
     * Call this twice in a row and watch the logs:
     *   1st call → "Cache MISS — fetching from PostgreSQL"
     *   2nd call → silence (Redis served it, method body skipped!)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
