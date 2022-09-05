package com.saravanank.ecommerce.resourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {

	private String applicationName;
	private String from;
	private String[] to;
	private String subject;
	private String url;
	private String name;
}
