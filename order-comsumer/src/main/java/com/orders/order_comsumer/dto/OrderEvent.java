package com.orders.order_comsumer.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * OrderEvent — must exactly match the shape published by order-api.
 * Kafka sends raw JSON bytes; this class tells Jackson how to deserialize them.
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
    private String eventType;
    private Instant timestamp;
}
