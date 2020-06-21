package com.jupitertools.springtestkafka.expected.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * By the use of this annotation, you can declare a list of
 * expected messages in JSON format, which you expect in the Kafka topic after the test execution.
 * Also, you can set a timeout, which will wait.
 *
 * @author Anatoliy Korovin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectedMessages {

	/**
	 * The name of a topic for messages
	 */
	String topic();

	/**
	 * The timeout for wait messages after the test execution (10 sec. by default)
	 */
	int timeout() default 10000;

	/**
	 * The path to the JSON file with expected messages
	 */
	String datasetFile();
}
