package com.saravanank.ecommerce.resourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saravanank.ecommerce.resourceserver.model.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

	public List<Transactions> findByOrderUserUserId(long userId);
	
	public List<Transactions> findByOrderOrderId(long orderId);
}
