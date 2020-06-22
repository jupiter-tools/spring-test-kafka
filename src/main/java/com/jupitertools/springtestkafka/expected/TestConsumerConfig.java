package com.jupitertools.springtestkafka.expected;


import java.util.Map;

/**
 * The properties holder for Kafka test consumer.
 *
 * @author Korovin Anatoliy
 */
public interface TestConsumerConfig {

	/**
	 * @return the the map of properties for the test Kafka consumer ({@link org.springframework.kafka.core.DefaultKafkaConsumerFactory})
	 */
	Map<String, Object> getProperties();
}
