package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.TransactionRequest;
import com.saravanank.ecommerce.resourceserver.repository.OrderRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;
import com.saravanank.ecommerce.resourceserver.util.Json;

@SpringBootTest
public class TransactionServiceTest {

	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private OrderRepository orderRepo;
	
	@Mock 
	private UserRepository userRepo;
	
	@Mock 
	private RabbitTemplate rabbitTemplate;
	
	@Value("${e-commerce.application.transaction-url}")
	private String transactionUrl;

	@Value("${e-commerce.application.client-id}")
	private String clientId;
	
	@InjectMocks
	private TransactionService transactionService;
	
	TransactionRequest req1 = new TransactionRequest(1, 100, 10, 20);
	HttpHeaders headers = new HttpHeaders();
	
	
	@Test
	public void putPayment_success() {
		when(orderRepo.existsById(10L)).thenReturn(true);
		when(userRepo.existsById(20L)).thenReturn(true);
		
		transactionService.putPaymentToQueue(req1);
		verify(rabbitTemplate, times(1)).convertAndSend(Json.toJson(req1).toString());
	}
	
	@Test
	public void putPayment_throwException() {
		TransactionRequest invalidReq1 = new TransactionRequest(1, 0, 10, 20);
		TransactionRequest invalidReq2 = new TransactionRequest(2, 100, 11, 20);
		TransactionRequest invalidReq3 = new TransactionRequest(3, 100, 10, 22);
		when(orderRepo.existsById(10L)).thenReturn(true);
		when(userRepo.existsById(20L)).thenReturn(true);
		when(orderRepo.existsById(11L)).thenReturn(false);
		when(userRepo.existsById(22L)).thenReturn(false);
		
		assertThrows(BadRequestException.class, () -> transactionService.putPaymentToQueue(invalidReq1), "Amount should be greater than 0");
		assertThrows(NotFoundException.class, () -> transactionService.putPaymentToQueue(invalidReq2), "Order with id 11 not found");
		assertThrows(NotFoundException.class, () -> transactionService.putPaymentToQueue(invalidReq3), "User with id 22 not found");
	}

	@Test
	public void getUserTransaction_success() throws JsonMappingException, JsonProcessingException {
		ReflectionTestUtils.setField(transactionService, "clientId", clientId);
		ReflectionTestUtils.setField(transactionService, "transactionUrl", transactionUrl);
		headers.set("X-Internal-Request", Base64.getEncoder().encodeToString(clientId.getBytes()));
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		when(restTemplate.exchange(transactionUrl + "/user/20" , HttpMethod.GET, httpEntity, String.class)).thenReturn(new ResponseEntity<String>("[{\"transactionId\": 1, \"amount\": 100}]", HttpStatus.OK));
		
		assertEquals(Json.parse("[{\"transactionId\": 1, \"amount\": 100}]"), transactionService.getUserTransactions(20));
	}
	
	@Test
	public void getOrderTransactions_success() throws JsonMappingException, JsonProcessingException {
		ReflectionTestUtils.setField(transactionService, "clientId", clientId);
		ReflectionTestUtils.setField(transactionService, "transactionUrl", transactionUrl);
		headers.set("X-Internal-Request", Base64.getEncoder().encodeToString(clientId.getBytes()));
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		when(restTemplate.exchange(transactionUrl + "/order/10" , HttpMethod.GET, httpEntity, String.class)).thenReturn(new ResponseEntity<String>("[{\"transactionId\": 5, \"amount\": 200}]", HttpStatus.OK));
		
		assertEquals(Json.parse("[{\"transactionId\": 5, \"amount\": 200}]"), transactionService.getOrderTransactions(10));
	}

}
