package com.saravanank.ecommerce.resourceserver.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.MobileNumberRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class MobileNumberServiceImpl implements MobileNumberService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MobileNumberRepository contactRepo;

	@Override
	public MobileNumber updateMobileNumber(long contactId, MobileNumber contact) {
		Optional<MobileNumber> mobileNumber = contactRepo.findById(contactId);
		if (mobileNumber.isEmpty())
			throw new NotFoundException("Mobile number with id " + contactId + " not found");
		MobileNumber contactData = mobileNumber.get();
		if (contact.getLabel() != null)
			contactData.setLabel(contact.getLabel());
		if (contact.getNumber() != null)
			contactData.setNumber(contact.getNumber());
		contactRepo.save(contactData);
		return contactData;
	}

	@Override
	public MobileNumber addMobileNumber(String username, MobileNumber number) {
		User userInDb = userRepo.findByUsername(username);
		if (userInDb == null)
			throw new NotFoundException("User with username " + username + " not found");
		userInDb.getMobileNumbers().add(number);
		userRepo.save(userInDb);
		return number;
	}

	@Override
	public void deleteMobileNumber(long contactId) {
		boolean numberExists = contactRepo.existsById(contactId);
		if (!numberExists)
			throw new NotFoundException("Mobile number with id " + contactId + " not found");
		contactRepo.deleteById(contactId);
	}

}
