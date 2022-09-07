package com.saravanank.ecommerce.resourceserver.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_quantity")
public class ProductQuantityMapper {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(cascade =  CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	@NotNull(message = "Product in product quantity mapping should not be null")
	private Product product;
	
	@PositiveOrZero(message = "Quantity in product quantity mapping should be positive or 0")
	private int quantity;

	public long getProductId() {
		return product.getProductId();
	}
 }
