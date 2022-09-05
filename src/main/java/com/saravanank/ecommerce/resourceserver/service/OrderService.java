package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;

import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;
import com.saravanank.ecommerce.resourceserver.model.TransactionRequest;

public interface OrderService {

	public List<Order> getUserOrders(String username);

	public List<Order> getUserOrders(long userId);

	public Order getById(long id);

	public List<Order> getAllOrders(String orderStatus, String search);

	public Order updateOrder(Order order, long orderId);

	public Order addOrder(String placedFor, String placedBy, List<ProductQuantityMapper> products, String paymentType);

	public Order placeOrderForUser(long userId, String placedBy, List<ProductQuantityMapper> products,
			String paymentType);

	public Order placeOrderFromCart(String paymentType, String username);

	public Order cancelOrder(long orderId, String cancelReason);

	public void recieveTransactions(TransactionRequest data);

}
