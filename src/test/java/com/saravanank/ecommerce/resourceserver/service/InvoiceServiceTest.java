package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Invoice;
import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.OrderStatus;
import com.saravanank.ecommerce.resourceserver.model.PaymentType;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.InvoiceRepository;

@SpringBootTest
public class InvoiceServiceTest {

	@Mock
	private InvoiceRepository invoiceRepo;

	@InjectMocks
	private InvoiceServiceImpl invoiceService;

	Date currentDate = new Date();
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.CUSTOMER, true, true, currentDate,
			currentDate, null, null, null);
	User testUser2 = new User(2, "User 2", "user2@gmail.com", "user2", "user2", Role.CUSTOMER, true, true, currentDate,
			currentDate, null, null, null);

	Order user1Order = new Order(1, 1000, currentDate, null, null, null, null, currentDate, 8, 1080, false, null,
			PaymentType.CASH_ON_DELIVERY, testUser1, testUser1, OrderStatus.PLACED, null);
	Order user2Order = new Order(2, 2000, currentDate, null, null, null, null, currentDate, 8, 2160, false, null,
			PaymentType.CASH_ON_DELIVERY, testUser2, testUser2, OrderStatus.PLACED, null);

	Invoice order1Invoice = new Invoice(1, testUser1, user1Order, 1080, 0, 1080, 0, 0, 0);
	Invoice order2Invoice = new Invoice(2, testUser2, user2Order, 2160, 0, 2160, 0, 0, 0);

	@Test
	public void getAllInvoice_success() {
		assertEquals(List.of(), invoiceService.getAll());

		when(invoiceRepo.findAll()).thenReturn(List.of(order1Invoice, order2Invoice));

		assertEquals(List.of(order1Invoice, order2Invoice), invoiceService.getAll());
	}

	@Test
	public void getById_success() {
		when(invoiceRepo.findById(1L)).thenReturn(Optional.of(order1Invoice));
		when(invoiceRepo.findById(2L)).thenReturn(Optional.of(order2Invoice));

		assertEquals(order1Invoice, invoiceService.getById(1));
		assertEquals(order2Invoice, invoiceService.getById(2));
	}

	@Test
	public void getById_throwException() {
		when(invoiceRepo.findById(3L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> invoiceService.getById(3), "Invoice with id 3 not found");
	}

	@Test
	public void getByOrderId_success() {
		when(invoiceRepo.findByOrderOrderId(1L)).thenReturn(order1Invoice);
		when(invoiceRepo.findByOrderOrderId(2L)).thenReturn(order2Invoice);

		assertEquals(order1Invoice, invoiceService.getByOrderId(1));
		assertEquals(order2Invoice, invoiceService.getByOrderId(2));
	}

	@Test
	public void getByOrderId_throwException() {
		when(invoiceRepo.findByOrderOrderId(3)).thenReturn(null);

		assertThrows(NotFoundException.class, () -> invoiceService.getByOrderId(3),
				"Invoice of order with id 3 not found");
	}
}
