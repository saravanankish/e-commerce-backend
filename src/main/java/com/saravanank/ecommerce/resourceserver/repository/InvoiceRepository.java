package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saravanank.ecommerce.resourceserver.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	public Invoice findByOrderOrderId(long orderId);
	
}
