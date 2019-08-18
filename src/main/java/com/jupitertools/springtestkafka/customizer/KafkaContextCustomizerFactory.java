package com.jupitertools.springtestkafka.customizer;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.jupitertools.springtestkafka.KafkaTestContainer;
import com.jupitertools.springtestkafka.KafkaTestContainers;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;



public class KafkaContextCustomizerFactory implements ContextCustomizerFactory {

	@Override
	public ContextCustomizer createContextCustomizer(Class<?> testClass,
	                                                 List<ContextConfigurationAttributes> list) {
		Set<KafkaTestContainer> annotations =
				AnnotationUtils.getRepeatableAnnotations(testClass,
				                                         KafkaTestContainer.class,
				                                         KafkaTestContainers.class);

		Set<String> descriptions = annotations.stream()
		                                      .map(KafkaTestContainer::bootstrapServersPropertyName)
		                                      .collect(Collectors.toSet());

		return new KafkaContainerContextCustomizer(descriptions);
	}
}
