package com.orders.order_api.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * OrderEvent — the message payload we publish to Kafka.
 *
 * This is NOT stored in the database. It's the "event" that travels
 * through Kafka to both order-consumer and notification-service.
 *
 * Think of it as a notification card that says:
 * "Hey, order X was placed for product Y by user Z"
 *
 * IMPORTANT: This DTO must be identical in all 3 services.
 * order-consumer and notification-service will deserialize this exact shape.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String eventType;    // "ORDER_PLACED"
    private Instant timestamp;
}
