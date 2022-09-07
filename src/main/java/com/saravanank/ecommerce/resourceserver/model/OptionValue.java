package com.saravanank.ecommerce.resourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionValue {

	private long id;
	private String name;
	private int quantity;
	
	public OptionValue(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
