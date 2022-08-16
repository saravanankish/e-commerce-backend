package com.saravanank.ecommerce.resourceserver.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "brand")
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	
	private Date creationDate;
	private Date modifiedDate;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "modified_by")
	private User modifiedBy;
	
}
