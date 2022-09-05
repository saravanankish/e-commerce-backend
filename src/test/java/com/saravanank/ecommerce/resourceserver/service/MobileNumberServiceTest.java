package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.MobileNumberRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;
import com.saravanank.ecommerce.resourceserver.util.Json;

@SpringBootTest
public class MobileNumberServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private MobileNumberRepository contactRepo;

	@InjectMocks
	private MobileNumberServiceImpl contactService;

	Date currentDate = new Date();
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.CUSTOMER, true, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	User testUser2 = new User(2, "User 2", "user2@gmail.com", "user2", "user2", Role.CUSTOMER, true, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);

	MobileNumber contact1 = new MobileNumber(1, "9876543210", null, false);
	MobileNumber contact2 = new MobileNumber(2, "9765432108", null, false);

	@Test
	public void updateMobileNumber_success() {
		MobileNumber updateContact1 = new MobileNumber(1, "8765432109", "Updated", false);

		when(contactRepo.findById(1L)).thenReturn(Optional.of(contact1));
		when(contactRepo.save(updateContact1)).thenReturn(updateContact1);

		assertEquals(updateContact1, contactService.updateMobileNumber(1, updateContact1));
	}

	@Test
	public void updateMobileNumber_throwException() {
		MobileNumber updateContact1 = new MobileNumber(1, "8765432109", null, false);
		when(contactRepo.findById(6L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> contactService.updateMobileNumber(6, updateContact1),
				"Mobile number with id 6 not found");
	}

	@Test
	public void addMobileNumber_success() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.findByUsername(testUser1.getUsername())).thenReturn(testUser1);

		User userRes = Json.fromJson(Json.toJson(testUser1), User.class);
		userRes.getMobileNumbers().add(contact1);

		assertEquals(contact1, contactService.addMobileNumber("user1", contact1));
		verify(userRepo, times(1)).save(userRes);
	}

	@Test
	public void addMobileNumber_throwException() {
		when(userRepo.findByUsername("invalid")).thenReturn(null);

		assertThrows(NotFoundException.class, () -> contactService.addMobileNumber("invalid", contact2),
				"User with username invalid not found");
	}

	@Test
	public void deleteMobileNumber_success() {
		when(contactRepo.existsById(2L)).thenReturn(true);

		contactService.deleteMobileNumber(2);
		verify(contactRepo, times(1)).deleteById(2L);
	}

	@Test
	public void deleteMobileNumber_throwException() {
		when(contactRepo.existsById(4L)).thenReturn(false);

		assertThrows(NotFoundException.class, () -> contactService.deleteMobileNumber(4),
				"Mobile number with id 4 not found");
	}

}
