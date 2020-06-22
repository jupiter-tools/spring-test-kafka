package com.jupitertools.springtestkafka.expected.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jupitertools.springtestkafka.expected.DefaultKafkaConsumerConfig;
import com.jupitertools.springtestkafka.expected.KafkaExpectedMessagesExtension;
import com.jupitertools.springtestkafka.expected.TestConsumerConfig;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Turn on the junit5 extension to work with DataSets in Kafka tests.
 * <p>
 * required for:
 * {@link ExpectedMessages}
 * {@link NoMessagesExpected}
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(KafkaExpectedMessagesExtension.class)
public @interface EnableKafkaTest {

	/**
	 * The list of topcis which will be processed during the test execution.
	 *
	 * @return com.jupitertools.springtestkafka.expected.annotation.ExpectedMessages should receive messages only from this topics
	 */
	String[] topics();

	/**
	 * The custom configuration provider for test Kafka Consumer.
	 * <p>
	 * You can declare specific properties for Kafka consumer in this file.
	 * For example, if you need to use an Integer key deserialization(instead of
	 * default StringDeserialization) then you can set all necessary properties
	 * in a separate file, and declare it here.
	 * <p>
	 * Be careful, this file will be instantiated manually,
	 * you cannot use dependency injection or make a private default
	 * constructor of this file. Also, this file cannot be a static nested class.
	 *
	 * @return class type of {@link TestConsumerConfig}
	 */
	Class<? extends TestConsumerConfig> testConsumerConfig() default DefaultKafkaConsumerConfig.class;
}
