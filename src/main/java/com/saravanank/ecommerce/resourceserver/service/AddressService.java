package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;

import com.saravanank.ecommerce.resourceserver.model.Address;

public interface AddressService {

	public Address addAddress(long userId, Address address);

	public Address addAddressByUsername(String username, Address address);

	public Address getAddressById(long addressId);

	public List<Address> getUserAddresses(long userId);

	public void deleteAddress(long addressId);

	public Address updateAddress(long addressId, Address address);

	public void changeDeliveryAddressOfUser(long addressId, String username);

}
