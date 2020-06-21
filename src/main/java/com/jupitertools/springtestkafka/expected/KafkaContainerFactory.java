package com.jupitertools.springtestkafka.expected;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

public class KafkaContainerFactory {


	public KafkaMessageListenerContainer createContainer(String bootstrapProperties,
	                                                     String[] topicNames,
	                                                     MessageListener kafkaTestConsumer) {

		Map<String, Object> props = consumerProps(bootstrapProperties);
		DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<Integer, String>(props);

		ContainerProperties containerProps = new ContainerProperties(topicNames);
		//new ContainerProperties(Pattern.compile("^.*$")); ? why this is'n working ?
		containerProps.setMessageListener(kafkaTestConsumer);

		KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(cf, containerProps);
		container.setBeanName("kafkaExpectedMessagesExtensionContainer");

		return container;
	}

	private Map<String, Object> consumerProps(String bootstrapProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-name");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return props;
	}
}
