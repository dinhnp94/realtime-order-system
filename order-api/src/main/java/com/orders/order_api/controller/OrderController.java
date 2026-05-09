package com.orders.order_api.controller;

import com.orders.order_api.dto.CreateOrderRequest;
import com.orders.order_api.model.Order;
import com.orders.order_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /orders — place a new order.
     * Full flow: Redis cache lookup → DB save → Kafka publish
     * @Valid triggers automatic validation of CreateOrderRequest fields
     */
    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /** GET /orders/{id} — get order by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    /** GET /orders?userId=user-123 — get all orders for a user */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByUser(@RequestParam String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }
}
