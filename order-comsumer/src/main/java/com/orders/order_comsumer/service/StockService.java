package com.orders.order_comsumer.service;

import com.orders.order_comsumer.dto.OrderEvent;
import com.orders.order_comsumer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * StockService — the brain of order-consumer. Does two things per order:
 *
 * 1. DECREMENT stock in PostgreSQL (atomic SQL UPDATE — no race conditions)
 * 2. INCREMENT leaderboard score in Redis (ZINCRBY)
 *
 * This is exactly the ZINCRBY you ran manually in redis-cli earlier!
 * After placing a few orders, check GET /stats/leaderboard on order-api
 * and you'll see the scores updating in real time.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LEADERBOARD_KEY = "product:leaderboard";

    @Transactional
    public void processOrder(OrderEvent event) {
        log.info("Processing order {} — product={}, qty={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        // Step 1: Atomically decrement stock in PostgreSQL
        // Returns 1 = success, 0 = insufficient stock
        int updated = productRepository.decrementStock(
                event.getProductId(), event.getQuantity());

        if (updated == 0) {
            log.warn("⚠️ Stock update failed for product {} (out of stock?). orderId={}",
                    event.getProductId(), event.getOrderId());
            return;
        }
        log.info("✅ Stock decremented for product {} by {}", event.getProductId(), event.getQuantity());

        // Step 2: Increment Redis leaderboard score
        // ZINCRBY product:leaderboard <quantity> <productId>
        Double newScore = redisTemplate.opsForZSet()
                .incrementScore(LEADERBOARD_KEY, event.getProductId(), event.getQuantity());

        log.info("📊 Leaderboard: product={} totalUnitsSold={}",
                event.getProductId(), newScore != null ? newScore.intValue() : 0);
    }
}
