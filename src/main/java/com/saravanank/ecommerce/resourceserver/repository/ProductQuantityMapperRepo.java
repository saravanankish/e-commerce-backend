package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;

public interface ProductQuantityMapperRepo extends JpaRepository<ProductQuantityMapper, Long> {

}
