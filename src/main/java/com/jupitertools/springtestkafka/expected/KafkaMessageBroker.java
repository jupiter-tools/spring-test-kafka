package com.jupitertools.springtestkafka.expected;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jupiter.tools.spring.test.core.expected.list.messages.MessageBroker;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;


public class KafkaMessageBroker implements MessageBroker {

	private final Logger log = LoggerFactory.getLogger(KafkaMessageBroker.class);
	private final ConcurrentHashMap<String, BlockingQueue<ConsumerRecord>> topicDataHolder = new ConcurrentHashMap<>();

	private final KafkaMessageListenerContainer testConsumerContainer;
	private final ObjectMapper objectMapper = new ObjectMapper();


	public KafkaMessageBroker(String bootstrapProperties, String[] topicNames) {
		KafkaTestConsumer kafkaTestConsumer = new KafkaTestConsumer(topicDataHolder);
		testConsumerContainer = createContainer(bootstrapProperties, topicNames, kafkaTestConsumer);
	}


	public void start() {
		testConsumerContainer.start();
		waitForAssign(testConsumerContainer);
	}

	private KafkaMessageListenerContainer createContainer(String bootstrapProperties,
	                                                      String[] topicNames,
	                                                      KafkaTestConsumer kafkaTestConsumer) {

		Map<String, Object> props = consumerProps(bootstrapProperties);
		DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<Integer, String>(props);

		ContainerProperties containerProps = new ContainerProperties(topicNames);
		//new ContainerProperties(Pattern.compile("^.*$")); ? why this is'n working ?
		containerProps.setMessageListener(kafkaTestConsumer);

		KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(cf, containerProps);
		container.setBeanName("kafkaExpectedMessagesExtensionContainer");

		return container;
	}


	@Override
	public Object receive(String queueName, long timeout) {
		topicDataHolder.computeIfAbsent(queueName, queue -> new LinkedBlockingQueue<>());
		try {

			ConsumerRecord consumerRecord = topicDataHolder.get(queueName)
			                                               .poll(timeout, TimeUnit.MILLISECONDS);
			if (consumerRecord == null) {
				return null;
			}

			return objectMapper.readValue((String) consumerRecord.value(), Map.class);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void reset() {
		log.info("reset KafkaTestConsumer.");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		topicDataHolder.clear();
	}


	static class KafkaTestConsumer<K, V> implements MessageListener<K, V> {

		private final ConcurrentHashMap<String, BlockingQueue<ConsumerRecord>> messageHolder;
		private final Logger log = LoggerFactory.getLogger(KafkaTestConsumer.class);

		public KafkaTestConsumer(ConcurrentHashMap<String, BlockingQueue<ConsumerRecord>> messageHolder) {
			this.messageHolder = messageHolder;
		}

		@Override
		public void onMessage(ConsumerRecord<K, V> data) {
			log.info("receive message: {}", data);
			messageHolder.computeIfAbsent(data.topic(), topic -> new LinkedBlockingDeque<>());
			messageHolder.get(data.topic()).add(data);
		}
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

	private void waitForAssign(MessageListenerContainer container) {
		log.debug("container before assign partitions: {}", container);
		Awaitility.await()
		          .pollDelay(50, TimeUnit.MILLISECONDS) // maybe is'n necessary?
		          .atMost(1, TimeUnit.MINUTES)
		          .until(() -> container.getAssignedPartitions() != null);
		log.debug("container after assign partitions: {}", container);
	}
}
