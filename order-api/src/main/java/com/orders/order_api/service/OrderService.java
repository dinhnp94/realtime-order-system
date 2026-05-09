package com.orders.order_api.service;

import com.orders.order_api.dto.CreateOrderRequest;
import com.orders.order_api.dto.OrderEvent;
import com.orders.order_api.kafka.OrderProducer;
import com.orders.order_api.model.Order;
import com.orders.order_api.model.Product;
import com.orders.order_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * OrderService — orchestrates the full order placement flow:
 *
 *   Step 1: Fetch product → comes from Redis if cached, PostgreSQL if not
 *   Step 2: Check stock availability
 *   Step 3: Save order to PostgreSQL (status = PENDING)
 *   Step 4: Publish OrderEvent to Kafka (async, doesn't block HTTP response)
 *
 * @Transactional ensures steps 1-3 are atomic — if anything fails,
 * the database rolls back. Kafka publish is outside the transaction
 * because Kafka is not a database (no rollback).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderProducer orderProducer;

    @Transactional
    public Order placeOrder(CreateOrderRequest request) {

        // Step 1: Get product (Redis cache hit or DB fetch)
        Product product = productService.getProductById(request.getProductId());

        // Step 2: Check stock
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException(String.format(
                    "Insufficient stock for '%s' (requested: %d, available: %d)",
                    product.getName(), request.getQuantity(), product.getStock()));
        }

        // Step 3: Save order with PENDING status
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        Order order = Order.builder()
                .userId(request.getUserId())
                .productId(product.getId())
                .quantity(request.getQuantity())
                .totalPrice(total)
                .build();

        order = orderRepository.save(order);
        log.info("Order {} saved → status=PENDING", order.getId());

        // Step 4: Publish to Kafka (fire and forget — async)
        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .productId(product.getId())
                .productName(product.getName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .eventType("ORDER_PLACED")
                .timestamp(Instant.now())
                .build();

        orderProducer.publishOrderEvent(event);

        return order;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
