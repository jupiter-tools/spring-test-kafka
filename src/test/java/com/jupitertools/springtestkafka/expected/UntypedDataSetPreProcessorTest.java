package com.jupitertools.springtestkafka.expected;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;


class UntypedDataSetPreProcessorTest {

	@Test
	void removeTypes() {
		// Arrange
		Map<String, Object> record1 = ImmutableMap.of("field1", 1,
		                                              "field2", "11");

		Map<String, Object> record2 = ImmutableMap.of("field1", 2,
		                                              "field2", "22");

		Map<String, Object> record3 = ImmutableMap.of("value", "DATA",
		                                              "id", 123);

		Map<String, List<Map<String, Object>>> map =
				ImmutableMap.of("first.class.ref", Arrays.asList(record1, record2),
				                "second.class.ref", Arrays.asList(record3));

		// Act
		Map<String, List<Map<String, Object>>> result = new UntypedDataSetPreProcessor().run(() -> map).read();

		// Assert
		assertThat(result).containsKeys("events");
		assertThat(result).containsValues(Arrays.asList(record1, record2, record3));
	}
}