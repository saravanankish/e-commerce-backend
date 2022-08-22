package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;

import com.saravanank.ecommerce.resourceserver.model.Invoice;

public interface InvoiceService {

	public List<Invoice> getAll();
	
	public Invoice getById(long id);
	
	public Invoice getByOrderId(long orderId);
	
}
