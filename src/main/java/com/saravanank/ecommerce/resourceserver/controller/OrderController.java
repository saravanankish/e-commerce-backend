package com.saravanank.ecommerce.resourceserver.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;
import com.saravanank.ecommerce.resourceserver.model.TransactionRequest;
import com.saravanank.ecommerce.resourceserver.service.OrderService;
import com.saravanank.ecommerce.resourceserver.util.Json;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/order")
public class OrderController {

	private static final Logger logger = Logger.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
	@ApiOperation(value = "Get order of current user", notes = "Only user with customer access can use this endpoint, returns orders of current authenticated user")
	public ResponseEntity<List<Order>> getOrdersOfUser(Principal principal) {
		logger.info("GET request to /api/v1/order");
		return new ResponseEntity<List<Order>>(orderService.getUserOrders(principal.getName()), HttpStatus.OK);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable("orderId") long orderId) {
		return new ResponseEntity<Order>(orderService.getById(orderId), HttpStatus.OK);
	}

	@GetMapping("/customer/{customerId}")
	@ApiOperation(value = "Get orders of customer", notes = "All users can use this endpoint")
	public ResponseEntity<List<Order>> getOrderOfCustomer(@PathVariable("customerId") long customerId) {
		logger.info("GET request to /api/v1/order/" + customerId);
		return new ResponseEntity<List<Order>>(orderService.getUserOrders(customerId), HttpStatus.OK);
	}

	@PostMapping("/cancel/{orderId}")
	@ApiOperation(value = "Cancel an order", notes = "All users can use this endpoint")
	public ResponseEntity<Order> cancelOrder(@PathVariable("orderId") long orderId, @RequestBody String cancelReason) {
		logger.info("POST request to /api/v1/order/cancel" + orderId);
		return new ResponseEntity<Order>(orderService.cancelOrder(orderId, cancelReason), HttpStatus.CREATED);
	}

	@GetMapping("/all")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPPORT')")
	@ApiOperation(value = "Get all orders", notes = "Only user with admin or support access can use this endpoint")
	public ResponseEntity<List<Order>> getAllOrders(
			@RequestParam(required = false, name = "orderStatus") String orderStatus,
			@RequestParam(required = false, name = "search") String search) {
		logger.info("GET request to /api/v1/order/all");
		return new ResponseEntity<List<Order>>(orderService.getAllOrders(orderStatus, search), HttpStatus.OK);
	}

	@PutMapping("/{orderId}")
	@ApiOperation(value = "Update an order", notes = "All users can use this endpoint")
	public ResponseEntity<Order> updateOrder(@PathVariable("orderId") long orderId, @RequestBody @Valid Order order) {
		logger.info("PUT request to /api/v1/order/" + orderId);
		return new ResponseEntity<Order>(orderService.updateOrder(order, orderId), HttpStatus.CREATED);
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
	@ApiOperation(value = "Add an order", notes = "All users can use this endpoint")
	public ResponseEntity<Order> addOrder(@RequestBody String data, Principal principal) {
		logger.info("POST request to /api/v1/order/add");
		try {
			JsonNode node = Json.parse(data);
			List<ProductQuantityMapper> map = Json.fromJsonAsList(Json.stringify(node.get("products")),
					ProductQuantityMapper.class);
			String paymentType = null;
			if (node.get("paymentType") != null)
				paymentType = node.get("paymentType").asText();
			return new ResponseEntity<Order>(
					orderService.addOrder(principal.getName(), principal.getName(), map, paymentType),
					HttpStatus.CREATED);
		} catch (JsonProcessingException | ClassNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/{userId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPPORT')")
	public ResponseEntity<Order> placeOrderForUser(@PathVariable("userId") long userId, @RequestBody String data,
			Principal principal) {
		logger.info("POST request to /api/v1/order/" + userId);
		try {
			JsonNode node = Json.parse(data);
			List<ProductQuantityMapper> map = Json.fromJsonAsList(Json.stringify(node.get("products")),
					ProductQuantityMapper.class);
			String paymentType = null;
			if (node.get("paymentType") != null)
				paymentType = node.get("paymentType").asText();
			return new ResponseEntity<Order>(
					orderService.placeOrderForUser(userId, principal.getName(), map, paymentType), HttpStatus.CREATED);
		} catch (JsonProcessingException | ClassNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/cart")
	@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
	public ResponseEntity<Order> placeOrderFromCart(Principal principal,
			@RequestBody(required = false) String paymentType) {
		logger.info("POST request to /api/v1/order/cart by user = " + principal.getName());
		return new ResponseEntity<Order>(orderService.placeOrderFromCart(paymentType, principal.getName()),
				HttpStatus.CREATED);
	}

	@PostMapping("/received/payment")
	public ResponseEntity<Void> handleReceivedPayment(@RequestBody TransactionRequest transaction) {
		orderService.recieveTransactions(transaction);
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

}
