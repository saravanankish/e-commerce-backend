package com.saravanank.ecommerce.resourceserver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mobile_number")
public class MobileNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long mobileNumberId;
	
	@NotEmpty(message = "Number should not be empty")
	@Pattern(regexp = "^\\d{10}$", message = "Invalid number format")
	private String number;
	private String label;
	
	private boolean primaryNumber;
}
