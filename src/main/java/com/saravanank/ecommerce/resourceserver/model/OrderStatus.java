package com.saravanank.ecommerce.resourceserver.model;

public enum OrderStatus {
	PENDING_PAYMENT,
	PLACED,
	IN_TRANSIT,
	OUT_FOR_DELIVERY,
	DELIVERED,
	CANCELED,
	RETURNED,
}
