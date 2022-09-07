package com.saravanank.ecommerce.resourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.saravanank.ecommerce.resourceserver.model.Brand;
import com.saravanank.ecommerce.resourceserver.model.OptionValue;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	public Brand findByNameIgnoreCase(String name);

	public List<Brand> findByNameContainingIgnoreCase(String name);
	
	@Query(value ="SELECT new com.saravanank.ecommerce.resourceserver.model.OptionValue(b.id, b.name) FROM Brand b")
	public List<OptionValue> findAllForOption();
	
}
