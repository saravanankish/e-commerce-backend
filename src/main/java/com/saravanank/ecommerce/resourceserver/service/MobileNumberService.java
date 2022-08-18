package com.saravanank.ecommerce.resourceserver.service;

import com.saravanank.ecommerce.resourceserver.model.MobileNumber;

public interface MobileNumberService {

	public MobileNumber updateMobileNumber(long contactId, MobileNumber contact);

	public MobileNumber addMobileNumber(String username, MobileNumber number);

	public void deleteMobileNumber(long contactId);

}
