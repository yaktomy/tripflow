
package com.tripflow.kafka;

import com.tripflow.kafka.event.TripCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Import(TripEventDltIntegrationTest.TestConfig.class)
class TripEventDltIntegrationTest {

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer("apache/kafka:4.0.0");

    @DynamicPropertySource
    static void configureKafka(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.kafka.bootstrap-servers",
                kafka::getBootstrapServers
        );
    }

    @Autowired
    private KafkaTemplate<String, TripCreatedEvent> kafkaTemplate;

    @Autowired
    private FailingEventConsumer failingEventConsumer;

    @Autowired
    private DeadLetterConsumer deadLetterConsumer;

    @Test
    void shouldSendFailedEventToDeadLetterTopic() throws Exception {
        TripCreatedEvent event = new TripCreatedEvent(
                200L,
                20L,
                "Trip to Japan"
        );

        kafkaTemplate.send(
                "trip-events",
                event.tripId().toString(),
                event
        ).get(10, TimeUnit.SECONDS);

        boolean received = deadLetterConsumer.latch.await(
                20,
                TimeUnit.SECONDS
        );

        assertTrue(
                received,
                "Failed event was not received from the DLT"
        );

        TripCreatedEvent receivedEvent =
                deadLetterConsumer.receivedEvent;

        assertNotNull(receivedEvent);
        assertEquals(event.tripId(), receivedEvent.tripId());
        assertEquals(event.ownerId(), receivedEvent.ownerId());
        assertEquals(event.title(), receivedEvent.title());

        assertEquals(
                3,
                failingEventConsumer.attempts,
                "Expected one initial attempt and two retries"
        );
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        NewTopic tripEventsTopic() {
            return new NewTopic("trip-events", 3, (short) 1);
        }

        @Bean
        NewTopic deadLetterTopic() {
            return new NewTopic("trip-events.DLT", 3, (short) 1);
        }

        @Bean
        FailingEventConsumer failingEventConsumer() {
            return new FailingEventConsumer();
        }

        @Bean
        DeadLetterConsumer deadLetterConsumer() {
            return new DeadLetterConsumer();
        }
    }

    static class FailingEventConsumer {

        volatile int attempts;

        @KafkaListener(
                topics = "trip-events",
                groupId = "tripflow-dlt-failure-test",
                containerFactory = "kafkaListenerContainerFactory",
                properties = "auto.offset.reset=earliest"
        )
        public void consume(TripCreatedEvent event) {
            if (event.tripId().equals(200L)) {
                attempts++;

                System.out.println(
                        "Intentional failure, attempt " + attempts
                );

                throw new IllegalStateException(
                        "Intentional failure for DLT integration test"
                );
            }
        }
    }

    static class DeadLetterConsumer {

        final CountDownLatch latch = new CountDownLatch(1);

        volatile TripCreatedEvent receivedEvent;

        @KafkaListener(
                topics = "trip-events.DLT",
                groupId = "tripflow-dlt-verification-test",
                containerFactory = "kafkaListenerContainerFactory",
                properties = "auto.offset.reset=earliest"
        )
        public void consume(TripCreatedEvent event) {
            System.out.println("DLT RECEIVED: " + event);

            receivedEvent = event;
            latch.countDown();
        }
    }
}