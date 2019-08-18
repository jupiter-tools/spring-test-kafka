package com.jupitertools.springtestkafka;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container for repeatable annotation {@link KafkaTestContainer}.
 *
 * Using to support a KafkaTestContainer as a repeatable annotation.
 *
 * Don't need to use this annotation directly, to run more than one
 * Kafka instance you can write multiple times of KafkaTestContainer
 * annotation in the single test file.
 *
 * @author Anatoliy Korovin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KafkaTestContainers {

	/**
	 * @return Container to multiple {@link KafkaTestContainer} annotations
	 */
	KafkaTestContainer[] value();
}
