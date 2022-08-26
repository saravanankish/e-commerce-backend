package com.saravanank.ecommerce.resourceserver.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.saravanank.ecommerce.resourceserver.util.Json;

@Service
public class TransactionService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${e-commerce.application.transaction-url}")
	private String transactionUrl;

	@Value("${e-commerce.application.client-id}")
	private String clientId;

	public JsonNode getUserTransactions(long userId) throws JsonMappingException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Internal-Request", Base64.getEncoder().encodeToString(clientId.getBytes()));
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> userTransactions = restTemplate.exchange(transactionUrl + "/user/" + userId,
				HttpMethod.GET, httpEntity, String.class);
		return Json.parse(userTransactions.getBody());
	}

	public JsonNode getOrderTransactions(long orderId) throws JsonMappingException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Internal-Request", Base64.getEncoder().encodeToString(clientId.getBytes()));
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> orderTransactions = restTemplate.exchange(transactionUrl + "/order/" + orderId,
				HttpMethod.GET, httpEntity, String.class);
		return Json.parse(orderTransactions.getBody());
	}
}
