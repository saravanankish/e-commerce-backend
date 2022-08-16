package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.saravanank.ecommerce.resourceserver.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
	@Query(nativeQuery =  true, value = "SELECT * FROM address WHERE user_id=?1 AND is_delivery_address=1")
	public Address findDeliveryAddressOfUser(long userId);
	
}
