package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.OrderStatus;
import com.saravanank.ecommerce.resourceserver.model.PaymentType;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.AddressRepository;
import com.saravanank.ecommerce.resourceserver.repository.CartRepository;
import com.saravanank.ecommerce.resourceserver.repository.InvoiceRepository;
import com.saravanank.ecommerce.resourceserver.repository.OrderRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;
import com.saravanank.ecommerce.resourceserver.util.Json;

@SpringBootTest
public class OrderServiceTest {

	@Mock
	private OrderRepository orderRepo;

	@Mock
	private UserRepository userRepo;

	@Mock
	private ProductRepository productRepo;

	@Mock
	private InvoiceRepository invoiceRepo;

	@Mock
	private AddressRepository addressRepo;

	@Mock
	private CartRepository cartRepo;

	@InjectMocks
	private OrderServiceImpl orderService;

	Date currentDate = new Date();
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.CUSTOMER, true, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	User testUser2 = new User(2, "User 2", "user2@gmail.com", "user2", "user2", Role.CUSTOMER, true, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	Order order1 = new Order(1, 100, currentDate, null, null, null, null, currentDate, 8, 108, false, null,
			PaymentType.CASH_ON_DELIVERY, testUser1, testUser1, OrderStatus.PENDING_PAYMENT, null);
	Order order2 = new Order(2, 200, currentDate, null, null, null, null, currentDate, 8, 216, false, null,
			PaymentType.CASH_ON_DELIVERY, testUser2, testUser2, OrderStatus.PENDING_PAYMENT, null);
	Order order3 = new Order(3, 300, currentDate, null, null, null, null, currentDate, 8, 324, false, null,
			PaymentType.CASH_ON_DELIVERY, testUser1, testUser1, OrderStatus.PENDING_PAYMENT, null);

	@Test
	public void getUserOrders_success() {
		when(userRepo.findByUsername(testUser1.getUsername())).thenReturn(testUser1);
		when(userRepo.findById(testUser2.getUserId())).thenReturn(Optional.of(testUser2));
		when(orderRepo.findByUserUsername(testUser1.getUsername())).thenReturn(List.of(order1, order3));
		when(orderRepo.findByUserUserId(testUser2.getUserId())).thenReturn(List.of(order2));

		assertEquals(List.of(order1, order3), orderService.getUserOrders("user1"));
		assertEquals(List.of(order2), orderService.getUserOrders(2));
	}

	@Test
	public void getUserOrders_throwException() {
		when(userRepo.findByUsername("invalid")).thenReturn(null);
		when(userRepo.findById(3L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> orderService.getUserOrders("invalid"),
				"User with username  not found");
		assertThrows(NotFoundException.class, () -> orderService.getUserOrders(3), "User with id 3 not found");
	}

	@Test
	public void getById_success() {
		when(orderRepo.findById(order1.getOrderId())).thenReturn(Optional.of(order1));

		assertEquals(order1, orderService.getById(1));
	}

	@Test
	public void getById_throwException() {
		when(orderRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> orderService.getById(4L), "Order with id 4 not found");
	}

	@Test
	public void getAllOrders_success() {
		when(orderRepo.findAll()).thenReturn(List.of(order1, order2, order3));

		assertEquals(List.of(order1, order2, order3), orderService.getAllOrders("", ""));
	}

	@Test
	public void cancelOrder_success() throws JsonProcessingException, IllegalArgumentException {
		when(orderRepo.findById(2L)).thenReturn(Optional.of(order2));

		Order cancelled = Json.fromJson(Json.toJson(order2), Order.class);
		cancelled.setOrderStatus(OrderStatus.CANCELED);
		cancelled.setCancelDate(new Date());
		cancelled.setCancelReason("Reason");
		cancelled.setModifiedDate(new Date());
		cancelled.setClosed(true);

		Order response = orderService.cancelOrder(2, "Reason");
		assertEquals("Reason", response.getCancelReason());
		assertEquals(OrderStatus.CANCELED, response.getOrderStatus());
	}

	@Test
	public void cancelOrder_throwException() {
		when(orderRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> orderService.cancelOrder(4, "Reason"), "Order with id 4 not found");
	}

}
