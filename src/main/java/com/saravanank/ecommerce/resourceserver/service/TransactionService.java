package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.saravanank.ecommerce.resourceserver.model.Invoice;
import com.saravanank.ecommerce.resourceserver.model.Order;
import com.saravanank.ecommerce.resourceserver.model.OrderStatus;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.Transactions;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.InvoiceRepository;
import com.saravanank.ecommerce.resourceserver.repository.OrderRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;
import com.saravanank.ecommerce.resourceserver.repository.TransactionRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private InvoiceRepository invoiceRepo;

	public Transactions makeTransaction(Transactions transaction) {
		Optional<Order> orderOpt = orderRepo.findById(transaction.getOrder().getOrderId());
		Order order = orderOpt.get();
		Invoice orderInvoice = invoiceRepo.findByOrderOrderId(order.getOrderId());
		if (transaction.isSuccess()) {
			if (transaction.getAmount() <= 0)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount should be greater than 0");
			if (orderOpt.isEmpty())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
			if (transaction.isReceived()) {
				float totalAmountReceived = orderInvoice.getTotalAmountReceived() + transaction.getAmount();
				float totalAmountPending = orderInvoice.getTotalAmountReceivable() - totalAmountReceived;
				orderInvoice.setTotalAmountReceived(totalAmountReceived);
				if (totalAmountPending < 0) {
					orderInvoice
							.setTotalAmountReturnable(totalAmountReceived - orderInvoice.getTotalAmountReceivable());
				} else {
					orderInvoice.setAmountPending(totalAmountPending);
				}
				if (totalAmountPending == orderInvoice.getTotalAmountReceivable()) {
					Date today = new Date();
					order.setOrderStatus(OrderStatus.PLACED);
					order.setModifiedDate(today);
					order.setExpectedDeliveryDate(new Date(today.getTime() + (7 * 24 * 60 * 60 * 1000)));
					orderRepo.save(order);
				} 
				orderInvoice.getTransactions().add(transaction);
				invoiceRepo.save(orderInvoice);
			}
			return transaction;
		} else {
			orderInvoice.getTransactions().add(transaction);
			invoiceRepo.save(orderInvoice);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction failed");
		}
	}

	public List<Transactions> getUserTransactrions(long userId) {
		boolean userExists = userRepo.existsById(userId);
		if (!userExists)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		return transactionRepo.findByOrderUserUserId(userId);
	}

	public List<Transactions> getOrderTransactrions(long orderId) {
		boolean orderExists = orderRepo.existsById(orderId);
		if (!orderExists)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		return transactionRepo.findByOrderOrderId(orderId);
	}
}
