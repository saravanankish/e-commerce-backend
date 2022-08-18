package com.saravanank.ecommerce.resourceserver.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Address;
import com.saravanank.ecommerce.resourceserver.model.Cart;
import com.saravanank.ecommerce.resourceserver.model.Invoice;
import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.OrderStatus;
import com.saravanank.ecommerce.resourceserver.model.PaymentType;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.AddressRepository;
import com.saravanank.ecommerce.resourceserver.repository.CartRepository;
import com.saravanank.ecommerce.resourceserver.repository.InvoiceRepository;
import com.saravanank.ecommerce.resourceserver.repository.OrderRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private InvoiceRepository invoiceRepo;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private AddressRepository addressRepo;

	@Autowired
	private CartRepository cartRepo;

	@Value("${e-commerce.application.tax-percentage}")
	private float taxPercentage;

	@Override
	public List<Order> getUserOrders(String username) {
		User userInDb = userRepo.findByUsername(username);
		if (userInDb == null) {
			throw new NotFoundException("User with username " + username + " not found");
		}
		logger.info("Returned order of user with username=" + username);
		return orderRepo.findByUserUsername(username);
	}

	@Override
	public List<Order> getUserOrders(long userId) {
		Optional<User> userInDb = userRepo.findById(userId);
		if (userInDb.isEmpty()) {
			throw new NotFoundException("User with id " + userId + " not found");
		}
		logger.info("Returned order of user with userId=" + userId);
		return orderRepo.findByUserUserId(userId);
	}

	@Override
	public List<Order> getAllOrders() {
		logger.info("Returned all order");
		return orderRepo.findAll();
	}

	@Override
	public Order updateOrder(Order order, long orderId) {
		Optional<Order> orderInDb = orderRepo.findById(orderId);
		if (orderInDb.isEmpty()) {
			throw new NotFoundException("Order with id " + orderId + " not found");
		}
		Order orderData = orderInDb.get();
		if (order.getValue() != 0)
			orderData.setValue(order.getValue());
		if (order.getOrderDate() != null)
			orderData.setOrderDate(order.getOrderDate());
		if (order.getOrderStatus() != null)
			orderData.setOrderStatus(order.getOrderStatus());
		if (order.getProducts() != null)
			orderData.setProducts(order.getProducts());
		if (order.getTotalValue() != 0)
			orderData.setTotalValue(order.getTotalValue());
		if (order.getTaxPercentage() != taxPercentage)
			orderData.setTaxPercentage(taxPercentage);
		if (order.getCancelDate() != null)
			orderData.setCancelDate(order.getCancelDate());
		if (order.getCancelReason() != null)
			orderData.setCancelReason(order.getCancelReason());
		if (order.getDeliveryAddress() != null)
			orderData.setDeliveryAddress(order.getDeliveryAddress());
		if (order.getDeliveryDate() != null)
			orderData.setDeliveryDate(order.getDeliveryDate());
		if (order.getExpectedDeliveryDate() != null)
			orderData.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
		if (order.getPaymentType() != null)
			orderData.setPaymentType(order.getPaymentType());
		if (order.getPlacedBy() != null)
			orderData.setPlacedBy(order.getPlacedBy());
		if (order.getUser() != null)
			orderData.setUser(order.getUser());
		orderData.setModifiedDate(new Date());
		orderRepo.saveAndFlush(orderData);
		logger.info("Updated order with orderId=" + orderId);
		return orderData;
	}

	@Override
	public Order addOrder(String placedFor, String placedBy, List<ProductQuantityMapper> products, String paymentType) {
		User placedForUser = userRepo.findByUsername(placedFor);
		User placedByUser = userRepo.findByUsername(placedBy);
		if (placedByUser == null || placedForUser == null) {
			throw new BadRequestException("User details are wrong, couldn't place order");
		}
		return orderHelper(placedForUser, placedByUser, products, paymentType);
	}
	
	@Override
	public Order placeOrderForUser(long userId, String placedBy, List<ProductQuantityMapper> products,
			String paymentType) {
		Optional<User> placedForUser = userRepo.findById(userId);
		User placedByUser = userRepo.findByUsername(placedBy);
		if (placedByUser == null || placedForUser.isEmpty()) {
			throw new BadRequestException("User details are wrong, couldn't place order");
		}
		return orderHelper(placedByUser, placedByUser, products, paymentType);
	}

	@Override
	public Order placeOrderFromCart(String paymentType, String username) {
		Cart userCart = cartRepo.findByUserUsername(username);
		User placedForUser = userRepo.findByUsername(username);
		if (placedForUser == null) {
			throw new BadRequestException("User details are wrong, couldn't place order");
		}
		if (userCart == null) {
			throw new NotFoundException("Cart of user with username " + username + " not found");
		}
		if (userCart.getProducts().size() == 0) {
			throw new BadRequestException("User cart is empty");
		}
		return orderHelper(placedForUser, placedForUser, userCart.getProducts(), paymentType);
	}

	private Order orderHelper(User placedForUser, User placedByUser, List<ProductQuantityMapper> products,
			String paymentType) {
		Order userOrder = new Order();
		Invoice invoice = new Invoice();
		float totalValue = 0;
		List<Product> productUpdate = new ArrayList<>();
		for (ProductQuantityMapper product : products) {
			Optional<Product> productData = productRepo.findById(product.getProductId());
			Product prod = productData.get();
			if (productData.isEmpty()) {
				throw new BadRequestException("One or more products not present, couldn't place order");
			}
			if (product.getQuantity() <= 0) {
				throw new BadRequestException("Quantity should be more than one, couldn't place order");
			}
			if(prod.getQuantity() < product.getQuantity()) {
				throw new BadRequestException("Insufficient stock of " + productData.get().getName());
			}
			prod.setQuantity(prod.getQuantity() - product.getQuantity());
			productUpdate.add(prod);
			totalValue += (product.getQuantity() * productData.get().getPrice());
		}
		productRepo.saveAll(productUpdate);
		Date currentDate = new Date();
		userOrder.setValue(totalValue);
		userOrder.setTotalValue(totalValue + (totalValue * (taxPercentage / 100)));
		userOrder.setPlacedBy(placedByUser);
		userOrder.setUser(placedForUser);
		userOrder.setTaxPercentage(taxPercentage);
		userOrder.setModifiedDate(currentDate);
		userOrder.setOrderDate(currentDate);
		userOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT);
		if (paymentType != null)
			userOrder.setPaymentType(PaymentType.valueOf(paymentType));
		userOrder.setProducts(products);
		Address deliveryAddress = addressRepo.findDeliveryAddressOfUser(placedForUser.getUserId());
		if (deliveryAddress == null)
			throw new BadRequestException("User has no delivery address");
		userOrder.setDeliveryAddress(deliveryAddress);
		invoice.setOrder(userOrder);
		invoice.setTotalAmountReceivable(totalValue + (totalValue * (taxPercentage / 100)));
		invoice.setAmountPending(totalValue + (totalValue * (taxPercentage / 100)));
		invoice.setUser(placedForUser);
		invoiceRepo.saveAndFlush(invoice);
		logger.info("Added order for user " + placedForUser.getUsername());
		return invoice.getOrder();
	}

	@Override
	public Order cancelOrder(long orderId, String cancelReason) {
		Optional<Order> order = orderRepo.findById(orderId);
		if(order.isEmpty()) throw new NotFoundException("Order with id " + orderId + " not found");
		Order orderData = order.get();
		orderData.setOrderStatus(OrderStatus.CANCELED);
		orderData.setCancelDate(new Date());
		orderData.setCancelReason(cancelReason);
		orderData.setModifiedDate(new Date());
		orderData.setClosed(true);
		orderRepo.saveAndFlush(orderData);
		return orderData;
	}
	
}
