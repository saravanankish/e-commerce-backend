package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;
import com.saravanank.ecommerce.resourceserver.util.Json;

@SpringBootTest
public class UserServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private CartService cartService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	Date currentDate = new Date();
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.CUSTOMER, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	User testUser2 = new User(2, "User 2", "user2@gmail.com", "user2", "user2", Role.CUSTOMER, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	org.springframework.security.core.userdetails.User securityTestUser1 = new org.springframework.security.core.userdetails.User(
			testUser1.getUsername(), testUser1.getPassword(), true, true, true, true,
			getAuthorities(Arrays.asList(testUser1.getRole().name())));
	org.springframework.security.core.userdetails.User securityTestUser2 = new org.springframework.security.core.userdetails.User(
			testUser2.getUsername(), testUser2.getPassword(), true, true, true, true,
			getAuthorities(Arrays.asList(testUser2.getRole().name())));

	private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
		// TODO Auto-generated method stub
		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		return authorities;
	}

	@Test
	public void loadbyUsername_success() {
		when(userRepo.findByUsername(testUser1.getUsername())).thenReturn(testUser1);

		assertEquals(securityTestUser1, userService.loadUserByUsername("user1"));
	}

	@Test
	public void loadByUsername_throwException() {
		when(userRepo.findByUsername("invalid")).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("invalid"), "No user found");
	}

	@Test
	public void getbyUsername_success() {
		when(userRepo.findByUsername(testUser2.getUsername())).thenReturn(testUser2);

		assertEquals(testUser2, userService.getUserByUsername("user2"));
	}

	@Test
	public void getByUsername_throwException() {
		when(userRepo.findByUsername("invalid")).thenReturn(null);

		assertThrows(NotFoundException.class, () -> userService.getUserByUsername("invalid"),
				"User with username invalid not found");
	}

	@Test
	public void getbyId_success() {
		when(userRepo.findById(testUser1.getUserId())).thenReturn(Optional.of(testUser1));

		assertEquals(testUser1, userService.getUserById(1));
	}

	@Test
	public void getById_throwException() {
		when(userRepo.findById(3L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.getUserById(3), "User with id 3 not found");
	}

	@Test
	public void deleteUser_success() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.findById(testUser2.getUserId())).thenReturn(Optional.of(testUser2));

		User tempUser = Json.fromJson(Json.toJson(testUser2), User.class);
		tempUser.setAccountActive(false);

		userService.deleteUser(2);
		verify(userRepo, times(1)).save(tempUser);
	}

	@Test
	public void deleteUser_throwException() {
		when(userRepo.findById(3L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.deleteUser(3), "User with id 3 not found");
	}

	@Test
	public void updateUser_success() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.findById(testUser2.getUserId())).thenReturn(Optional.of(testUser2));
		when(userRepo.findByUsername("test1")).thenReturn(testUser1);

		User updatedUser = Json.fromJson(Json.toJson(testUser2), User.class);
		updatedUser.setName("User 2 updated");
		updatedUser.setUsername("user2u");
		updatedUser.setModifiedTime(new Date());
		updatedUser.setModifiedBy(testUser1);

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
		userService.updateUser(updatedUser, 2, "test1");
		verify(userRepo, times(1)).save(argument.capture());
	}

	@Test
	public void updateUser_throwException() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.findById(3L)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> userService.updateUser(testUser1, 3, "test1"),
				"User with id 3 not found");
	}

	@Test
	public void updateUserWithoutCustomerId_throwException() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.findById(1L)).thenReturn(Optional.of(testUser1));

		assertThrows(NotFoundException.class, () -> userService.updateUser(testUser1, 0, "user1"),
				"User id is not present");
	}

	@Test
	public void getByRole_success() {
		PageRequest req = PageRequest.of(0, 5);
		Page<User> users = new PageImpl<>(List.of(testUser1, testUser2), req, 2);
		when(userRepo.findByRole(Role.CUSTOMER, req)).thenReturn(users);

		PageResponseModel<User> response = userService.getByRole("customer", 0, 5, null);
		assertEquals(2, response.getData().size());
		assertEquals(2, response.getTotal());
		assertEquals(1, response.getTotalPages());
		assertEquals(0, response.getCurrentPage());
		assertEquals(5, response.getLimit());
	}
	
	@Test
	public void getByRoleEmpty_success() {
		PageRequest req = PageRequest.of(0, 5);
		Page<User> users = new PageImpl<>(List.of(), req, 0);
		when(userRepo.findByRole(Role.ADMIN, req)).thenReturn(users);
		
		PageResponseModel<User> response = userService.getByRole("admin", 0, 5, null);
		assertEquals(0, response.getData().size());
		assertEquals(0, response.getTotal());
		assertEquals(0, response.getTotalPages());
		assertEquals(0, response.getCurrentPage());
		assertEquals(5, response.getLimit());
	}
	
	@Test
	public void addUser_success() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.existsByUsername("user1")).thenReturn(false);
		when(passwordEncoder.encode(testUser1.getPassword())).thenReturn("encodedPassword");
		
		User addUser = Json.fromJson(Json.toJson(testUser1), User.class);
		addUser.setPassword("encodedPassword");
		addUser.setCreationTime(new Date());
		addUser.setModifiedTime(new Date());
		
		userService.addUser(testUser1);
		ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
		verify(cartService, times(1)).addCartToUser(argument.capture());
		verify(userRepo, times(1)).save(argument.capture());
	}
	
	@Test
	public void addUser_throwException() {
		when(userRepo.existsByUsername("user1")).thenReturn(true);
		
		assertThrows(BadRequestException.class, () -> userService.addUser(testUser1), "Username already present");
	}
	
}
