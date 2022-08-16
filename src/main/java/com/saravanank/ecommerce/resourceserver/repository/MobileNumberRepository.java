package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saravanank.ecommerce.resourceserver.model.MobileNumber;

public interface MobileNumberRepository extends JpaRepository<MobileNumber, Long> {
	
}
