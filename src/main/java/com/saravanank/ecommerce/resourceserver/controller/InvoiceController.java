package com.saravanank.ecommerce.resourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saravanank.ecommerce.resourceserver.model.Invoice;
import com.saravanank.ecommerce.resourceserver.service.InvoiceService;

@RestController
@RequestMapping("/v1/invoice")
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<List<Invoice>> getAllInvoice() {
		return new ResponseEntity<List<Invoice>>(invoiceService.getAll(), HttpStatus.OK);
	}

	@GetMapping("{invoiceId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Invoice> getById(@PathVariable("invoiceId") long id) {
		return new ResponseEntity<Invoice>(invoiceService.getById(id), HttpStatus.OK);
	}

	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Invoice> getInvoiceOfOrder(@PathVariable("orderId") long orderId) {
		return new ResponseEntity<Invoice>(invoiceService.getByOrderId(orderId), HttpStatus.OK);
	}

}
