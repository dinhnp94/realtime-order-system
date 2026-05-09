package com.orders.order_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig — creates the "orders" topic on startup if it doesn't exist.
 *
 * Why do this in code instead of manually?
 * → Reproducible. Any developer or server that runs this app automatically
 *   gets the correct topic with the right settings. No manual setup needed.
 *
 * Partitions = 3: allows 3 consumers to read in parallel (one per partition).
 * Replicas = 1: fine for local dev. In production on AWS MSK, you'd use 3.
 */
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.orders}")
    private String ordersTopic;

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name(ordersTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
