package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Address;
import com.saravanank.ecommerce.resourceserver.model.DeliveryTimingType;
import com.saravanank.ecommerce.resourceserver.model.Json;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.AddressRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@SpringBootTest
public class AddressServiceTest {

	@Mock
	private AddressRepository addressRepo;

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private AddressServiceImpl addressService;

	MobileNumber testMobileNumber = new MobileNumber(1L, "9876543210", "test", false);

	Address address1 = new Address(1, "19", "Street 1", "Area 1", "City 1", "State 1", "600001", "Landmark 1", "Test 1",
			false, DeliveryTimingType.HOME, testMobileNumber);
	Address address2 = new Address(2, "11", "Street 2", "Area 2", "City 2", "State 2", "600002", "Landmark 2", "Test 2",
			false, DeliveryTimingType.OFFICE, testMobileNumber);
	Address address3 = new Address(3, "12", "Street 3", "Area 3", "City 3", "State 3", "600003", "Landmark 3", "Test 3",
			false, DeliveryTimingType.HOME, testMobileNumber);
	Address address4 = new Address(4, "22", "Street 4", "Area 4", "City 4", "State 4", "600004", "Landmark 4", "Test 4",
			true, DeliveryTimingType.OFFICE, testMobileNumber);
	User testUser1 = new User(1, "test1", "test1", "test1", "test1", Role.ADMIN, true, null, null, null, null,
			new ArrayList<Address>(List.of(address1, address3)));
	User testUser2 = new User(2, "test2", "test2", "test2", "test2", Role.CUSTOMER, true, null, null, null, null,
			new ArrayList<Address>(List.of(address2, address4)));

	@Test
	public void getAddressById_success() {
		when(addressRepo.findById(address3.getId())).thenReturn(Optional.of(address3));

		assertEquals(address3, addressService.getAddressById(3));
	}

	@Test
	public void getAddressById_throwException() {
		assertThrows(NotFoundException.class, () -> addressService.getAddressById(5));
	}

	@Test
	public void getUserAddresses_success() {
		when(userRepo.findById(1L)).thenReturn(Optional.of(testUser1));

		assertEquals(List.of(address1, address3), addressService.getUserAddresses(1));
	}

	@Test
	public void getUserAddresses_throwException() {
		assertThrows(NotFoundException.class, () -> addressService.getUserAddresses(1));
	}

	@Test
	public void deleteAddress_success() {
		when(addressRepo.existsById(address2.getId())).thenReturn(true);

		addressService.deleteAddress(2);
		verify(addressRepo, times(1)).deleteById(2L);
	}

	@Test
	public void deleteAddress_throwException() {
		assertThrows(NotFoundException.class, () -> addressService.deleteAddress(5));
	}

	@Test
	public void addAddress_success() {
		when(userRepo.findById(testUser2.getUserId())).thenReturn(Optional.of(testUser2));

		assertEquals(address3, addressService.addAddress(2, address3));
		assertEquals(3, testUser2.getAddresses().size());
		assertEquals(List.of(address2, address4, address3), testUser2.getAddresses());
	}

	@Test
	public void addAddress_throwException() {
		when(userRepo.existsById(3L)).thenThrow(NotFoundException.class);

		assertThrows(NotFoundException.class, () -> addressService.addAddress(3, address1));
	}

	@Test
	public void addAddressByUsername_success() {
		when(userRepo.findByUsername(testUser1.getUsername())).thenReturn(testUser1);

		assertEquals(address4, addressService.addAddressByUsername("test1", address4));
		assertEquals(3, testUser1.getAddresses().size());
		assertEquals(List.of(address1, address3, address4), testUser1.getAddresses());
	}

	@Test
	public void addAddressByUsername_throwException() {
		when(userRepo.findByUsername("Unknown")).thenThrow(NotFoundException.class);

		assertThrows(NotFoundException.class, () -> addressService.addAddressByUsername("Unknown", address1));
	}

	@Test
	public void changeDeliveryAddress_success() {
		when(userRepo.findByUsername("test1")).thenReturn(testUser1);

		addressService.changeDeliveryAddressOfUser(1, "test1");
		for (Address address : testUser1.getAddresses()) {
			if (address.getId() == 1)
				assertTrue(address.isDeliveryAddress());
			else
				assertFalse(address.isDeliveryAddress());
		}
	}

	@Test
	public void changeDeliveryAddress_throwException() {
		when(userRepo.findByUsername("test1")).thenReturn(testUser1);

		assertThrows(NotFoundException.class, () -> addressService.changeDeliveryAddressOfUser(5, "test1"));
	}

	@Test
	public void updateAddress_success() throws JsonProcessingException, IllegalArgumentException {
		when(addressRepo.findById(address3.getId())).thenReturn(Optional.of(address3));

		Address tempUpdatedAddress = Json.fromJson(Json.toJson(address3), Address.class);
		tempUpdatedAddress.setArea("Area 3 updated");
		tempUpdatedAddress.setCity("City 3 updated");
		assertEquals(tempUpdatedAddress, addressService.updateAddress(3, tempUpdatedAddress));
		assertEquals(tempUpdatedAddress, address3);
	}

	@Test
	public void updateAddress_throwException() {
		when(addressRepo.findById(5L)).thenThrow(NotFoundException.class);

		assertThrows(NotFoundException.class, () -> addressService.updateAddress(5, address4));
	}

}
