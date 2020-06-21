package com.jupitertools.springtestkafka.expected.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jupitertools.springtestkafka.expected.KafkaExpectedMessagesExtension;
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
}
