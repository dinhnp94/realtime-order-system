package com.orders.order_api.kafka;

import com.orders.order_api.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * OrderProducer — publishes OrderEvent messages to Kafka.
 *
 * KafkaTemplate is Spring's wrapper around Kafka's Producer API.
 * Under the hood it handles: serialization, partitioning, retries, and acknowledgments.
 *
 * We use orderId as the message KEY so that all events for the same order
 * always land on the same Kafka partition (guarantees ordering per order).
 *
 * send() is ASYNC — it returns a CompletableFuture so the HTTP response
 * is NOT blocked waiting for Kafka. The callback just logs success or failure.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topic.orders}")
    private String ordersTopic;

    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing to Kafka topic '{}': orderId={}", ordersTopic, event.getOrderId());

        CompletableFuture<SendResult<String, OrderEvent>> future =
                kafkaTemplate.send(ordersTopic, event.getOrderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Published to partition={} offset={}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Failed to publish orderId={}: {}", event.getOrderId(), ex.getMessage());
            }
        });
    }
}
