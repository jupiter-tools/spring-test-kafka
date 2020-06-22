package com.jupitertools.springtestkafka.expected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jupiter.tools.spring.test.core.expected.list.messages.DataSetPreProcessor;
import com.jupitertools.datasetroll.DataSet;

/**
 * Replace class-ref in the data-set on the same string value (`events`),
 * and collect all entries in this key.
 *
 * @author Korovin Anatoliy
 */
public class UntypedDataSetPreProcessor implements DataSetPreProcessor {

	@Override
	public DataSet run(DataSet source) {

		Map<String, List<Map<String, Object>>> sourceMap = source.read();
		Map<String, List<Map<String, Object>>> result = new HashMap<>();

		result.put("events", new ArrayList<>());
		sourceMap.forEach((k, v) -> result.get("events").addAll(v));

		return () -> result;
	}
}
