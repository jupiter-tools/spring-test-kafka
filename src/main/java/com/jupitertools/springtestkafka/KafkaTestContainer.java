package com.jupitertools.springtestkafka;


import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Start a docker container with the Apache Kafka
 * and set host/port to default spring boot properties.
 *
 * @author Anatoliy Korovin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(KafkaTestContainers.class)
public @interface KafkaTestContainer {

	/**
	 * This property uses to set a host/port value of bootstrap servers
	 * after start testcontainer with Kafka.
	 *
	 * When your need a more than one testcontainer of Kafka in your test then
	 * use {@link KafkaTestContainer} multiple times and set this property in
	 * different values.
	 *
	 * @return In this property will be set a value of bootstrap server of
	 * Kafka testcontainer after the start it.
	 */
	String bootstrapServersPropertyName() default "spring.kafka.bootstrap-servers";
}
