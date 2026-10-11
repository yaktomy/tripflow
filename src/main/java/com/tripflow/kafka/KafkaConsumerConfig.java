package com.tripflow.kafka;

import com.tripflow.kafka.event.TripCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, TripCreatedEvent> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "tripflow-group"
        );
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.JsonDeserializer"
        );
        properties.put(
                "spring.json.trusted.packages",
                "com.tripflow.kafka.event"
        );
        properties.put(
                "spring.json.value.default.type",
                "com.tripflow.kafka.event.TripCreatedEvent"
        );

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, TripCreatedEvent> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) -> new TopicPartition(
                                record.topic() + ".DLT",
                                record.partition()
                        )
                );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 2L)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TripCreatedEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, TripCreatedEvent> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, TripCreatedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);

        return factory;
    }
}