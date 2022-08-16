package com.saravanank.ecommerce.resourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saravanank.ecommerce.resourceserver.model.Transactions;
import com.saravanank.ecommerce.resourceserver.service.TransactionService;

@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping
	public ResponseEntity<Transactions> makePayment(@RequestBody Transactions transaction,
			@PathVariable("userId") long userId) {
		return new ResponseEntity<Transactions>(HttpStatus.CREATED);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Transactions>> getUserTransactions(@PathVariable("userId") long userId) {
		return new ResponseEntity<List<Transactions>>(transactionService.getUserTransactrions(userId), HttpStatus.OK);
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<List<Transactions>> getOrderTransactions(@PathVariable("orderId") long orderId) {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
