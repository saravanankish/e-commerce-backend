package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Invoice;
import com.saravanank.ecommerce.resourceserver.repository.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {
	
	@Autowired
	private InvoiceRepository invoiceRepo;

	@Override
	public List<Invoice> getAll() {
		return invoiceRepo.findAll();
	}

	@Override
	public Invoice getById(long id) {
		Optional<Invoice> invoiceInDb = invoiceRepo.findById(id);
		if(invoiceInDb.isEmpty()) 
			throw new NotFoundException("Invoice with id " + id + " not found");
		return invoiceInDb.get();
	}

	@Override
	public Invoice getByOrderId(long orderId) {
		Invoice invoiceInDb = invoiceRepo.findByOrderOrderId(orderId);
		if(invoiceInDb == null) 
			throw new NotFoundException("Invoice of order with id " + orderId + " not found");
		return invoiceInDb;
	}

}
