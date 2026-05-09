package com.orders.order_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * StatsController — serves the real-time sales leaderboard from Redis.
 *
 * Remember ZREVRANGE from redis-cli? That's exactly what this does.
 * Every time order-consumer processes an order, it runs ZINCRBY on this key.
 * This endpoint just reads the current sorted set — pure Redis, no DB query.
 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LEADERBOARD_KEY = "product:leaderboard";

    /** GET /stats/leaderboard?limit=10 — top selling products from Redis */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {

        // ZREVRANGE product:leaderboard 0 9 WITHSCORES
        Set<ZSetOperations.TypedTuple<Object>> results =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(LEADERBOARD_KEY, 0, limit - 1);

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;

        if (results != null) {
            for (ZSetOperations.TypedTuple<Object> entry : results) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("rank", rank++);
                item.put("productId", entry.getValue());
                item.put("unitsSold", entry.getScore() != null ? entry.getScore().intValue() : 0);
                leaderboard.add(item);
            }
        }

        return ResponseEntity.ok(leaderboard);
    }
}
