package com.jupitertools.springtestkafka;

import java.util.Date;

import com.jupitertools.springtestkafka.expected.KafkaExpectedMessagesExtension;
import com.jupitertools.springtestkafka.expected.annotation.EnableKafkaTest;
import com.jupitertools.springtestkafka.expected.annotation.ExpectedMessages;
import com.jupitertools.springtestkafka.expected.annotation.NoMessagesExpected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@KafkaTestContainer
@EnableKafkaTest(topics = {"test-topic", "wow"})
@TestPropertySource(properties = {
		"spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
		"spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
class ExpectedMessagesTest {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Test
	@ExpectedMessages(topic = "test-topic", datasetFile = "/datasets/expected_simple.json")
	void firstTopic() {
		kafkaTemplate.send("test-topic", Foo.builder()
		                                .value("qwert")
		                                .build());

		kafkaTemplate.send("test-topic", Bar.builder()
		                                .name("baaark")
		                                .build());
	}

	@Test
	@ExpectedMessages(topic = "test-topic", datasetFile = "/datasets/expected_without_class_ref.json")
	void withoutTypeRef() {
		kafkaTemplate.send("test-topic", Foo.builder()
		                                .value("qwert")
		                                .build());

		kafkaTemplate.send("test-topic", Bar.builder()
		                                .name("baaark")
		                                .build());
	}

	@Test
	@ExpectedMessages(topic = "wow", datasetFile = "/datasets/expected_with_dynamic_matching.json")
	void anotherTopic() {
		kafkaTemplate.send("wow", Bar.builder().time(new Date()).build());
	}

	@Test
	@NoMessagesExpected(timeout = 3000)
	void silence() {

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
