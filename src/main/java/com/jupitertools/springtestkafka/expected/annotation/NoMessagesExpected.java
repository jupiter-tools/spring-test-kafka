package com.jupitertools.springtestkafka.expected.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You can use this annotation in tests which not send something in Kafka to check the silence.
 * <p>
 * Waits during the timeout for a messages in Kafka topics(set in {@link EnableKafkaTest})
 * if receive something then throws an exception.
 *
 * @author Anatoliy Korovin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoMessagesExpected {

	/**
	 * The timeout for wait messages after the test execution (10 sec. by default)
	 */
	int timeout() default 10000;
}
