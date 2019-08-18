package com.jupitertools.springtestkafka;


import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Start a docker container with Apache Kafka
 * and set host/port to default spring boot properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(KafkaTestContainers.class)
public @interface KafkaTestContainer {

	String bootstrapServersPropertyName() default "spring.kafka.bootstrap-servers";
}
