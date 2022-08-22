package com.saravanank.ecommerce.resourceserver.model;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {

	private static ObjectMapper objectMapper = getObjectMapper();

	private static ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		return objectMapper;
	}
	
	public static JsonNode parse(String src) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readTree(src);
	}

	public static <A> A fromJson(JsonNode node, Class<A> className)
			throws JsonProcessingException, IllegalArgumentException {
		return objectMapper.treeToValue(node, className);
	}
	
	public static JsonNode toJson(Object data) {
		return objectMapper.valueToTree(data);
	}

	public static <A> List<A> fromJsonAsList(String src, Class<A> a)
			throws JsonMappingException, JsonProcessingException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<A[]> arrayClass = (Class<A[]>) Class.forName("[L" + a.getName() + ";");
		A[] objects = objectMapper.readValue(src, arrayClass);
		return Arrays.asList(objects);
	}

	public static String stringify(JsonNode node) throws JsonProcessingException {
		return generateString(node, false);
	}

	public static String prettyPrint(JsonNode node) throws JsonProcessingException {
		return generateString(node, true);
	}

	public static String generateString(JsonNode node, boolean pretty) throws JsonProcessingException {
		ObjectWriter writer = objectMapper.writer();
		if (pretty)
			writer = writer.with(SerializationFeature.INDENT_OUTPUT);
		return writer.writeValueAsString(node);
	}

}
