package com.orders.notification_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Must match the OrderEvent published by order-api exactly. */
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
