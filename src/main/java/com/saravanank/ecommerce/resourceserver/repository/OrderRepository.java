package com.saravanank.ecommerce.resourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saravanank.ecommerce.resourceserver.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	public List<Order> findByUserUsername(String username);
	
	public List<Order> findByUserUserId(long userId);
	
}
