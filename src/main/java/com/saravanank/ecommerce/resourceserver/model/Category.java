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
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotEmpty(message = "Category name should be empty")
	private String name;

	@ElementCollection
	private List<@NotEmpty(message = "Sub category cannot be empty") String> subCategory;

	private Date creationDate = new Date();
	private Date modifiedDate = new Date();

	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "modified_by")
	private User modifiedBy;
}
