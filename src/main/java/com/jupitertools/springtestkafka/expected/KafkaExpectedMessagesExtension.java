package com.jupitertools.springtestkafka.expected;


import com.jupiter.tools.spring.test.core.expected.list.messages.AssertReceivedMessages;
import com.jupiter.tools.spring.test.core.expected.list.messages.DataSetPreProcessor;
import com.jupiter.tools.spring.test.core.expected.list.messages.ExpectedMessagesOptions;
import com.jupitertools.springtestkafka.expected.annotation.EnableKafkaTest;
import com.jupitertools.springtestkafka.expected.annotation.ExpectedMessages;
import com.jupitertools.springtestkafka.expected.annotation.NoMessagesExpected;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Junit5 extension to work with the data-sets in Kafka tests.
 *
 * see {@link EnableKafkaTest}
 * see {@link ExpectedMessages}
 * see {@link NoMessagesExpected}
 *
 * @author Anatoliy Korovin
 */
public class KafkaExpectedMessagesExtension implements BeforeAllCallback,
                                                       BeforeEachCallback,
                                                       AfterEachCallback {

	private KafkaMessageBroker kafkaMessageBroker;

	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		String bootstrap = getBootstrapProperties(extensionContext);
		String[] topics = getTopics(extensionContext);
		kafkaMessageBroker = new KafkaMessageBroker(bootstrap, topics);
		kafkaMessageBroker.start();
	}


	@Override
	public void afterEach(ExtensionContext extensionContext) throws Exception {

		if (isExpectedSilence(extensionContext)) {
			assertSilence(extensionContext);
			return;
		}

		if (isExpectedMessages(extensionContext)) {
			assertMessages(extensionContext);
		}
	}

	private boolean isExpectedSilence(ExtensionContext extensionContext) {

		NoMessagesExpected noMessagesExpected = extensionContext.getRequiredTestMethod()
		                                                        .getAnnotation(NoMessagesExpected.class);

		ExpectedMessages expectedMessages = extensionContext.getRequiredTestMethod()
		                                                    .getAnnotation(ExpectedMessages.class);
		if (noMessagesExpected == null) {
			return false;
		}

		if (expectedMessages != null) {
			throw new Error("Wrong expected state, you can use only one annotation: NoMessagesExpected or ExpectedMessages, simultaneously");
		}

		return true;
	}

	private void assertSilence(ExtensionContext context) {

		NoMessagesExpected noMessagesExpected = context.getRequiredTestMethod()
		                                               .getAnnotation(NoMessagesExpected.class);

		ExpectedMessagesOptions options = ExpectedMessagesOptions.builder()
		                                                         .allQueues(getTopics(context))
		                                                         .timeout(noMessagesExpected.timeout())
		                                                         .build();

		new AssertReceivedMessages(options, kafkaMessageBroker).doAssertSilence();
	}

	private boolean isExpectedMessages(ExtensionContext extensionContext) {

		ExpectedMessages expectedMessages = extensionContext.getRequiredTestMethod()
		                                                    .getAnnotation(ExpectedMessages.class);

		return expectedMessages != null;
	}


	private void assertMessages(ExtensionContext extensionContext) {

		ExpectedMessages expectedMessages = extensionContext.getRequiredTestMethod()
		                                                    .getAnnotation(ExpectedMessages.class);

		DataSetPreProcessor dataSetPreProcessor = new UntypedDataSetPreProcessor();
		ExpectedMessagesOptions options = ExpectedMessagesOptions.builder()
		                                                         .messagesFile(expectedMessages.datasetFile())
		                                                         .queue(expectedMessages.topic())
		                                                         .timeout(expectedMessages.timeout())
		                                                         .actualDataSetPreProcessor(dataSetPreProcessor)
		                                                         .expectedDataSetPreProcessor(dataSetPreProcessor)
		                                                         .build();

		new AssertReceivedMessages(options, kafkaMessageBroker).doAssert();
	}



	private String[] getTopics(ExtensionContext extensionContext) {

		EnableKafkaTest enableKafkaTest = extensionContext.getRequiredTestClass()
		                                                  .getAnnotation(EnableKafkaTest.class);
		return enableKafkaTest.topics();
	}


	private String getBootstrapProperties(ExtensionContext extensionContext) {
		String bootsrap = SpringExtension.getApplicationContext(extensionContext)
		                                 .getEnvironment()
		                                 .getProperty("spring.kafka.bootstrap-servers");
		Assertions.assertNotNull(bootsrap, "Not found Kafka bootstap host:port in properties");
		return bootsrap;
	}

	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		kafkaMessageBroker.reset();
	}
}
