package com.jupitertools.springtestkafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.assertj.core.api.Assertions.assertThat;


@KafkaTestContainer(bootstrapServersPropertyName = "first.kafka.server")
@KafkaTestContainer(bootstrapServersPropertyName = "second.kafka.server")
@SpringBootTest
class MultipleKafkaTestContainersTest {

	@Autowired
	@Qualifier("firstKafkaTemplate")
	public KafkaTemplate<String, String> firstKafkaTemplate;

	@Autowired
	@Qualifier("secondKafkaTemplate")
	public KafkaTemplate<String, String> secondKafkaTemplate;

	@Test
	void firstKafkaTest() {
		testKafkaServer(firstKafkaTemplate, "first-topic", FirstKafkaServerTestConfig.firstListenerMessages);
		testKafkaServer(secondKafkaTemplate, "second-topic", SecondKafkaServerTestConfig.secondListenerMessages);
	}

	private void testKafkaServer(KafkaTemplate<String, String> kafkaTemplate,
	                             String topicName,
	                             List<String> listenerInvocations) {
		// Arrange
		assertThat(firstKafkaTemplate).isNotNull();
		// Act
		kafkaTemplate.send(topicName, "antkorwin", "flight of a dragon");
		// Wait
		Awaitility.await()
		          .atMost(5, TimeUnit.SECONDS)
		          .until(() -> listenerInvocations.size() > 0);
		// Assert
		assertThat(listenerInvocations).hasSize(1)
		                               .contains("flight of a dragon");
	}

	@TestConfiguration
	public static class FirstKafkaServerTestConfig {

		static List<String> firstListenerMessages = new ArrayList<>();

		//region Kafka Consumer
		@KafkaListener(topics = "first-topic", containerFactory = "kafkaListenerContainerFactory")
		public void onKafkaEvent(String message) {
			firstListenerMessages.add(message);
		}

		@Bean("firstConsumerFactory")
		public ConsumerFactory<String, String> firstConsumerFactory(@Value("${first.kafka.server}") String firstKafkaServer) {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, firstKafkaServer);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "antkorwin-first-kafka-server-consumer-group");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			return new DefaultKafkaConsumerFactory<>(props);
		}

		@Bean("kafkaListenerContainerFactory")
		public ConcurrentKafkaListenerContainerFactory<String, String> firstContainerFactory(
				@Qualifier("firstConsumerFactory") ConsumerFactory<String, String> firstConsumerFactory) {

			ConcurrentKafkaListenerContainerFactory<String, String> factory =
					new ConcurrentKafkaListenerContainerFactory<>();

			factory.setConsumerFactory(firstConsumerFactory);
			return factory;
		}
		//endregion Kafka Consumer

		//region Kafka Producer
		@Bean("firstKafkaTemplate")
		public KafkaTemplate<String, String> firstKafkaTemplate(
				@Qualifier("firstProducerFactory") ProducerFactory<String, String> firstProducerFactory) {

			return new KafkaTemplate<>(firstProducerFactory);
		}

		@Bean("firstProducerFactory")
		public ProducerFactory<String, String> firstProducerFactory(@Value("${first.kafka.server}") String firstKafkaServer) {
			Map<String, Object> configProps = new HashMap<>();
			configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, firstKafkaServer);
			configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			return new DefaultKafkaProducerFactory<>(configProps);
		}
		//endregion Kafka Producer
	}

	@TestConfiguration
	public static class SecondKafkaServerTestConfig {

		static List<String> secondListenerMessages = new ArrayList<>();

		//region Kafka Consumer
		@KafkaListener(topics = "second-topic", containerFactory = "secondContainerFactory")
		public void secondKafkaEvent(String message) {
			secondListenerMessages.add(message);
		}

		@Bean("secondConsumerFactory")
		public ConsumerFactory<String, String> firstConsumerFactory(@Value("${second.kafka.server}") String secondKafkaServer) {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, secondKafkaServer);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "antkorwin-second-kafka-server-consumer-group");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			return new DefaultKafkaConsumerFactory<>(props);
		}

		@Bean("secondContainerFactory")
		public ConcurrentKafkaListenerContainerFactory<String, String> secondContainerFactory(
				@Qualifier("secondConsumerFactory") ConsumerFactory<String, String> secondConsumerFactory) {

			ConcurrentKafkaListenerContainerFactory<String, String> factory =
					new ConcurrentKafkaListenerContainerFactory<>();

			factory.setConsumerFactory(secondConsumerFactory);
			return factory;
		}
		//endregion Kafka Consumer

		//region Kafka Producer
		@Bean("secondKafkaTemplate")
		public KafkaTemplate<String, String> secondKafkaTemplate(
				@Qualifier("secondProducerFactory") ProducerFactory<String, String> secondProducerFactory) {

			return new KafkaTemplate<>(secondProducerFactory);
		}

		@Bean("secondProducerFactory")
		public ProducerFactory<String, String> secondProducerFactory(@Value("${second.kafka.server}") String secondKafkaServer) {
			Map<String, Object> configProps = new HashMap<>();
			configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, secondKafkaServer);
			configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			return new DefaultKafkaProducerFactory<>(configProps);
		}
		//endregion Kafka Producer
	}
}
