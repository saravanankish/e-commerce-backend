package com.saravanank.ecommerce.resourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.saravanank.ecommerce.resourceserver.model.TransactionRequest;
import com.saravanank.ecommerce.resourceserver.service.TransactionService;


@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/user/{userId}")
	public ResponseEntity<String> makePayment(@RequestBody TransactionRequest transaction) {
		transactionService.putPaymentToQueue(transaction);
		return new ResponseEntity<String>("Payment processing", HttpStatus.ACCEPTED);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<JsonNode> getUserTransactions(@PathVariable("userId") long userId) throws JsonMappingException, JsonProcessingException {
		return new ResponseEntity<JsonNode>(transactionService.getUserTransactions(userId), HttpStatus.OK);
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<JsonNode> getOrderTransactions(@PathVariable("orderId") long orderId) throws JsonMappingException, JsonProcessingException {
		return new ResponseEntity<JsonNode>(transactionService.getOrderTransactions(orderId),HttpStatus.OK);
	}

}
