package com.jupitertools.springtestkafka.expected;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;


public class KafkaContainerFactory {

	public KafkaMessageListenerContainer createContainer(String bootstrapProperties,
	                                                     String[] topicNames,
	                                                     MessageListener kafkaTestConsumer,
	                                                     Class<? extends TestConsumerConfig> consumerConfigClass) {

		Map<String, Object> props = prepareConsumerProperties(consumerConfigClass, bootstrapProperties);
		DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<Integer, String>(props);

		ContainerProperties containerProps = new ContainerProperties(topicNames);
		//new ContainerProperties(Pattern.compile("^.*$")); ? why this is'n working ?
		containerProps.setMessageListener(kafkaTestConsumer);

		KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(cf, containerProps);
		container.setBeanName("kafkaExpectedMessagesExtensionContainer");

		return container;
	}


	private Map<String, Object> prepareConsumerProperties(Class<? extends TestConsumerConfig> consumerConfigClass,
	                                                      String bootstrapProperties) {
		try {
			TestConsumerConfig consumerConfig = consumerConfigClass.getDeclaredConstructor().newInstance();
			Map<String, Object> properties = consumerConfig.getProperties();
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
			return properties;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to instantiate a class with TestConsumerConfig", e);
		}
	}
}
