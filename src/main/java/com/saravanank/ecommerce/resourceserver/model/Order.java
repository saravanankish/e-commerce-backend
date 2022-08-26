package com.saravanank.ecommerce.resourceserver.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderId;
	
	@Min(value = 1, message = "Value should be greater than one")
	private float value;
	private Date orderDate;
	private Date expectedDeliveryDate;
	private Date deliveryDate;
	private Date cancelDate;
	private String cancelReason;
	private Date modifiedDate;
	
	@Min(value = 0, message = "Tax percentage should be greater than 0")
	@Max(value = 100, message = "Tax percentage should be less than 100")
	private float taxPercentage;
	@Min(value = 1, message = "Value should be greater than one")
	private float totalValue;
	private boolean isClosed = false;
	
	@NotNull(message = "Delivery address should not be null")
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_address")
	private Address deliveryAddress;
	
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@NotNull(message = "User of order should not be null")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "placed_by")
	private User placedBy;
	
	@NotNull(message = "Order status should not be null")
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@NotNull(message = "Order should contain atleast one product")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id")
	private List<@NotNull(message = "Product quantity mapper should not be null") ProductQuantityMapper> products;

}
