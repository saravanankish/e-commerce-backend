package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Address;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.AddressRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public Address addAddress(long userId, Address address) {
		Optional<User> user = userRepo.findById(userId);
		if (user.isEmpty())
			throw new NotFoundException("User with id " + userId + " not found");
		User userData = user.get();
		List<Address> userAddress = userData.getAddresses();
		userAddress.add(address);
		userData.setAddresses(userAddress);
		userRepo.save(userData);
		return address;
	}

	@Override
	public Address addAddressByUsername(String username, Address address) {
		User user = userRepo.findByUsername(username);
		if (user == null)
			throw new NotFoundException("User with username " + username + " not found");
		List<Address> userAddress = user.getAddresses();
		userAddress.add(address);
		user.setAddresses(userAddress);
		userRepo.save(user);
		return address;
	}

	@Override
	public Address getAddressById(long addressId) {
		Optional<Address> address = addressRepo.findById(addressId);
		if (address.isEmpty())
			throw new NotFoundException("Address with id " + addressId + " not found");
		return address.get();
	}

	@Override
	public List<Address> getUserAddresses(long userId) {
		Optional<User> user = userRepo.findById(userId);
		if (user.isEmpty())
			throw new NotFoundException("User with id " + userId + " not found");
		return user.get().getAddresses();
	}

	@Override
	public void deleteAddress(long addressId) {
		boolean addressExists = addressRepo.existsById(addressId);
		if (!addressExists)
			throw new NotFoundException("Address with id " + addressId + " not found");
		addressRepo.deleteById(addressId);
	}

	@Override
	public Address updateAddress(long addressId, Address address) {
		Optional<Address> addressInDb = addressRepo.findById(addressId);
		if (addressInDb.isEmpty())
			throw new NotFoundException("Address with id " + addressId + " not found");
		Address addressData = addressInDb.get();
		if (address.getDoorNo() != null)
			addressData.setDoorNo(address.getDoorNo());
		if (address.getStreet() != null)
			addressData.setStreet(address.getStreet());
		if (address.getState() != null)
			addressData.setState(address.getState());
		if (address.getArea() != null)
			addressData.setArea(address.getArea());
		if (address.getCity() != null)
			addressData.setCity(address.getCity());
		if (address.getPincode() != 0)
			addressData.setPincode(address.getPincode());
		if (address.getLandmark() != null)
			addressData.setLandmark(address.getLandmark());
		if (address.getLabel() != null)
			addressData.setLabel(address.getLabel());
		if (address.getDeliveryTimeType() != null)
			addressData.setDeliveryTimeType(address.getDeliveryTimeType());
		if (address.getPhoneNumber() != null)
			addressData.setPhoneNumber(address.getPhoneNumber());
		addressRepo.save(addressData);
		return addressData;
	}

	@Override
	public void changeDeliveryAddressOfUser(long addressId, String username) {
		User user = userRepo.findByUsername(username);
		if (user == null)
			throw new NotFoundException("User with username " + username + " not found");
		boolean addressExistsForUser = false;
		List<Address> userAddress = user.getAddresses();
		for(Address address: userAddress) {
			if (address.getId() == addressId) {
				address.setDeliveryAddress(true);
				addressExistsForUser = true;
				return;
			} else {
				address.setDeliveryAddress(false);
			}
		}
		if(!addressExistsForUser) {
			throw new NotFoundException("Address with id " + addressId + " is not present for user " + username);
		}
		user.setAddresses(userAddress);
		userRepo.save(user);
	}

}
