package com.orders.notification_service.kafka;

import com.orders.notification_service.dto.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * NotificationConsumer — listens to the SAME "orders" topic as order-consumer,
 * but belongs to a DIFFERENT consumer group ("notifiers").
 *
 * This is Kafka's fan-out pattern:
 *   - One message published by order-api
 *   - order-processors group receives it → updates stock + leaderboard
 *   - notifiers group receives it → sends email notification
 *
 * Both groups get 100% of all messages, completely independently.
 * They don't know about each other. This is how you build decoupled microservices.
 *
 * In production: replace log.info with AWS SES, SendGrid, Twilio, etc.
 */
@Component
@Slf4j
public class NotificationConsumer {

    @KafkaListener(
            topics = "${app.kafka.topic.orders}",
            groupId = "notifiers"
    )
    public void handleOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("📧 [notifiers] Received from partition={} offset={}", partition, offset);

        // Simulate sending an email notification
        log.info("📬 Sending email to user '{}': Your order for '{}' (x{}) is confirmed! Order ID: {}",
                event.getUserId(),
                event.getProductName(),
                event.getQuantity(),
                event.getOrderId());
    }
}
