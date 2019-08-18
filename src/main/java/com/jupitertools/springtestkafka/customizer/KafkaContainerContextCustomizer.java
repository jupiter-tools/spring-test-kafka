package com.jupitertools.springtestkafka.customizer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;


public class KafkaContainerContextCustomizer implements ContextCustomizer {

	private static final Logger log = LoggerFactory.getLogger(KafkaContainerContextCustomizer.class);
	private final Set<String> bootstrapProperties;

	public KafkaContainerContextCustomizer(Set<String> bootstrapPropertyNames) {
		this.bootstrapProperties = bootstrapPropertyNames;
	}

	@Override
	public void customizeContext(ConfigurableApplicationContext configurableApplicationContext,
	                             MergedContextConfiguration mergedContextConfiguration) {

		for (String bootstrap : bootstrapProperties) {
			log.debug("Try to start Kafka TestContainer -> [{}]", bootstrap);
			KafkaContainer kafka = new KafkaContainer();
			kafka.start();
			log.debug("Started Kafka TestContainer at:[{}]", kafka.getBootstrapServers());

			TestPropertyValues testPropertyValues =
					TestPropertyValues.of(String.format("%s=%s",
					                                    bootstrap,
					                                    kafka.getBootstrapServers()));

			testPropertyValues.applyTo(configurableApplicationContext);
		}
	}
}
