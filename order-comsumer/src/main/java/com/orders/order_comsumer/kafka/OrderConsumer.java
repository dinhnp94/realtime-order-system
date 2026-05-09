package com.orders.order_comsumer.kafka;

import com.orders.order_comsumer.dto.OrderEvent;
import com.orders.order_comsumer.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * OrderConsumer — listens to the "orders" topic as consumer group "order-processors".
 *
 * @KafkaListener is the consumer-side equivalent of kafkaTemplate.send() on the producer.
 * Spring automatically:
 *   - Connects to Kafka on startup
 *   - Polls for new messages in a background thread
 *   - Deserializes JSON bytes → OrderEvent object
 *   - Calls this method for every new message
 *   - Commits the offset after successful processing (so it's not reprocessed)
 *
 * partition and offset headers help you see WHICH partition the message came from —
 * this is very educational to observe in the logs.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final StockService stockService;

    @KafkaListener(
            topics = "${app.kafka.topic.orders}",
            groupId = "order-processors"
    )
    public void handleOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("📥 [order-processors] Received from partition={} offset={} | orderId={}",
                partition, offset, event.getOrderId());

        try {
            stockService.processOrder(event);
        } catch (Exception e) {
            log.error("❌ Error processing orderId={}: {}", event.getOrderId(), e.getMessage(), e);
            // Production note: send to Dead Letter Topic (DLT) instead of dropping the message
        }
    }
}
