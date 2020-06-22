package com.jupitertools.springtestkafka.expected;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jupiter.tools.spring.test.core.expected.list.messages.MessageBroker;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

/**
 * {@link MessageBroker} implementation to read data from Kafka topics.
 *
 * @author Korovin Anatoliy
 */
public class KafkaMessageBroker implements MessageBroker {

	private final Logger log;
	private final ConcurrentHashMap<String, BlockingQueue<ConsumerRecord>> topicDataHolder;
	private final KafkaMessageListenerContainer testConsumerContainer;
	private final ObjectMapper objectMapper;


	public KafkaMessageBroker(String bootstrapProperties, String[] topicNames) {
		this.log = LoggerFactory.getLogger(KafkaMessageBroker.class);
		this.objectMapper = new ObjectMapper();
		this.topicDataHolder = new ConcurrentHashMap<>();

		KafkaTestConsumer kafkaTestConsumer = new KafkaTestConsumer(topicDataHolder);
		KafkaContainerFactory kafkaContainerFactory = new KafkaContainerFactory();

		testConsumerContainer = kafkaContainerFactory.createContainer(bootstrapProperties,
		                                                              topicNames,
		                                                              kafkaTestConsumer);
	}

	/**
	 * Create KafkaMessageListenerContainer and start receiving messages.
	 */
	public void start() {
		testConsumerContainer.start();
		waitForAssign(testConsumerContainer);
	}

	/**
	 * retrieve a new message from Kafka topic
	 *
	 * @param queueName topic name
	 * @param timeout   timeout
	 * @return received messages or null
	 */
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

	/**
	 * Drop all received messages from Kafka
	 */
	public void reset() throws InterruptedException {
		log.info("reset KafkaTestConsumer.");
		Thread.sleep(100);
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

	private void waitForAssign(MessageListenerContainer container) {
		log.debug("container before assign partitions: {}", container);
		Awaitility.await()
		          .pollDelay(50, TimeUnit.MILLISECONDS) // maybe is'n necessary?
		          .atMost(1, TimeUnit.MINUTES)
		          .until(() -> container.getAssignedPartitions() != null);
		log.debug("container after assign partitions: {}", container);
	}
}
