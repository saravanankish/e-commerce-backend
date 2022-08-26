package com.saravanank.ecommerce.resourceserver.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotEmpty(message = "Door number should not be empty")
	private String doorNo;
	@NotEmpty(message = "Street name should not be empty")
	private String street;
	@NotEmpty(message = "Area should not be empty")
	private String area;
	@NotEmpty(message = "City should not be empty")
	private String city;
	@NotEmpty(message = "State should not be empty")
	private String state;

	@NotEmpty(message = "Pincode should not be empty")
	@Pattern(regexp = "^[1-9]{1}[0-9]{2}\\s{0, 1}[0-9]{3}$", message = "Invalid pincode")
	private String pincode;
	@NotEmpty(message = "Landmark should not be empty")
	private String landmark;
	
	private String label;
	private boolean isDeliveryAddress = false;
	
	@NotNull(message = "Delivery time type should not be null")
	@Enumerated(EnumType.STRING)
	private DeliveryTimingType deliveryTimeType;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "contact_id")
	private MobileNumber phoneNumber;
	
}
