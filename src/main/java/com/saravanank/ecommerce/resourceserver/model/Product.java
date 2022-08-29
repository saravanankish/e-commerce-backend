package com.saravanank.ecommerce.resourceserver.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;
	
	@NotEmpty(message = "Product name should not be empty")
	private String name;
	@NotEmpty(message = "Product description should not be empty")
	private String description;
	@Min(value = 1, message = "Product price should be greater than 1")
	private float price;
	@Min(value = 0, message = "Product quantity should be greater than 0")
	private Integer quantity;
	@Min(value = 0, message = "Products rating should be greater than 0")
	@Max(value = 5, message = "Products rating should be less than 5")
	private float rating;
	@NotEmpty(message = "Product thumbnail url should not be empty")
	private String thumbnail;
	
	@ElementCollection
	private List<@NotEmpty(message = "Image url of product should not be empty") String> images;
	
	@NotNull(message = "Product brand should not be null")
	@OneToOne(cascade = CascadeType.REFRESH)
	private Brand brand;
	
	@NotEmpty(message = "Product category should not be empty")
	private String category;
	private Date creationDate = new Date();
	private Date modifiedDate = new Date();
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "modified_by")
	private User modifiedBy;
	
}
