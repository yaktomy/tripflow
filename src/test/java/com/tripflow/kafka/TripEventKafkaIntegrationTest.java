
package com.tripflow.kafka;

import com.tripflow.kafka.event.TripCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
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
class TripEventKafkaIntegrationTest {

    @Container
    static KafkaContainer kafka =
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
    private TestEventConsumer testEventConsumer;

    @Autowired
    private KafkaListenerEndpointRegistry listenerRegistry;

    @Test
    void shouldSendAndReceiveTripCreatedEvent() throws Exception {
        System.out.println(
                "Kafka listener containers: " +
                        listenerRegistry.getListenerContainers().size()
        );

        listenerRegistry.getListenerContainers().forEach(container ->
                System.out.println(
                        "Listener running: " + container.isRunning()
                )
        );

        TripCreatedEvent event = new TripCreatedEvent(
                100L,
                10L,
                "Trip to Italy"
        );

        kafkaTemplate.send(
                "trip-events",
                event.tripId().toString(),
                event
        ).get(10, TimeUnit.SECONDS);

        boolean received = testEventConsumer.latch.await(
                10,
                TimeUnit.SECONDS
        );

        assertTrue(received, "Kafka consumer did not receive the event");

        TripCreatedEvent receivedEvent = testEventConsumer.receivedEvent;

        assertNotNull(receivedEvent);
        assertEquals(event.tripId(), receivedEvent.tripId());
        assertEquals(event.ownerId(), receivedEvent.ownerId());
        assertEquals(event.title(), receivedEvent.title());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        TestEventConsumer testEventConsumer() {
            return new TestEventConsumer();
        }
    }

    static class TestEventConsumer {

        final CountDownLatch latch = new CountDownLatch(1);

        volatile TripCreatedEvent receivedEvent;

        @KafkaListener(
                topics = "trip-events",
                groupId = "tripflow-integration-test-group",
                containerFactory = "kafkaListenerContainerFactory",
                properties = "auto.offset.reset=earliest"
        )
        public void consume(TripCreatedEvent event) {
            System.out.println("TEST CONSUMER RECEIVED: " + event);
            receivedEvent = event;
            latch.countDown();
        }
    }
}