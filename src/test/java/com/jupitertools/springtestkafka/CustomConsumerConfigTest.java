package com.jupitertools.springtestkafka;

import java.util.Date;

import com.jupitertools.springtestkafka.expected.annotation.EnableKafkaTest;
import com.jupitertools.springtestkafka.expected.annotation.ExpectedMessages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@KafkaTestContainer
@EnableKafkaTest(topics = "integer-key-test-topic",
                 testConsumerConfig = IntegerKeyKafkaConsumerConfig.class)
@TestPropertySource(properties = {
		"spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
		"spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.IntegerSerializer"
})
class CustomConsumerConfigTest {

	@Autowired
	private KafkaTemplate<Integer, Object> kafkaTemplate;

	@Test
	@ExpectedMessages(topic = "integer-key-test-topic", datasetFile = "/datasets/expected_without_class_ref.json")
	void withIntKeys() {
		kafkaTemplate.send("integer-key-test-topic", 1, Foo.builder().value("qwert").build());
		kafkaTemplate.send("integer-key-test-topic", 2, Bar.builder().name("baaark").build());
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	static class Foo {
		private String value;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	static class Bar {
		private String name;
		private Date time;
	}
}
