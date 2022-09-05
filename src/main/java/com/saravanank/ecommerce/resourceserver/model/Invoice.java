package com.saravanank.ecommerce.resourceserver.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long invoiceId;
	
	@NotNull(message = "User of invoice should not be null")
	@OneToOne(cascade =  CascadeType.REFRESH)
	@JoinColumn(name ="invoice_of_user")
	private User user;
	
	@NotNull(message = "Order of invoice should not be null")
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "invoice_of_order")
	private Order order;
	
	private float totalAmountReceivable;
	private float totalAmountReceived;
	private float amountPending;
	
	private float totalAmountReturnable;
	private float totalAmountReturned;
	private float pendingReturns;
	
}
