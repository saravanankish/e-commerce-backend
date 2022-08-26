package com.saravanank.ecommerce.resourceserver.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToMany(cascade = CascadeType.MERGE)
	@JoinColumn(name = "cart_id")
	private List<ProductQuantityMapper> products;
	
	@NotNull(message = "Cart user should not be null")
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user")
	private User user;
	
}
