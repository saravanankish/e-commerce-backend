package com.saravanank.ecommerce.resourceserver.model;

import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

	private long transactionId;
		
	@Positive(message = "Amount should be greater than 0")
	private float amount;

	@Positive(message = "Order id should be greater than 0")
	private long orderId;

	@Positive(message = "User id should be greater than 0")
	private long userId;

}
