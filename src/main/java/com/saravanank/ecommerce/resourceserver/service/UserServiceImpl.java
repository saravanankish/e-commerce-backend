package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.MailRequest;
import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;
import com.saravanank.ecommerce.resourceserver.util.Json;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${e-commerce.application.frontend-url}")
	private String frontendUrl;

	@Value("${e-commerce.application.notification-exchange}")
	private String notificationExchange;

	@Value("${spring.rabbitmq.template.routing-key}")
	private String routingKey;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("No user found");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true,
				true, true, user.isEmailVerified(), getAuthorities(Arrays.asList(user.getRole().name())));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
		// TODO Auto-generated method stub
		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		return authorities;
	}

	@Override
	public User addUser(User user) throws JsonProcessingException {
		if (userRepo.existsByUsername(user.getUsername())) {
			throw new BadRequestException("Username already present");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreationTime(new Date());
		user.setModifiedTime(new Date());
		userRepo.save(user);
		if (user.getRole().equals(Role.CUSTOMER)) {
			MailRequest mailRequest = new MailRequest();
			mailRequest.setApplicationName("E-commerce");
			mailRequest.setTo(new String[] { user.getEmail() });
			mailRequest.setSubject("Verify email of " + user.getName());
			mailRequest.setName(user.getName());
			String encoded = Base64.getEncoder().encodeToString(user.getUsername().getBytes());
			mailRequest.setUrl(frontendUrl + "/verify/mail/" + encoded);
			System.out.println(encoded);
			System.out.println(new String(Base64.getDecoder().decode(encoded)));
			
			rabbitTemplate.convertAndSend(notificationExchange, routingKey, Json.stringify(Json.toJson(mailRequest)));
			cartService.addCartToUser(user);
		}
		logger.info("Added user with userid=" + user.getUserId());
		return user;
	}

	@Override
	public User getUserByUsername(String username) {
		User user = userRepo.findByUsername(username);
		if (user == null)
			throw new NotFoundException("User with username " + username + " not found");
		logger.info("Returned user with username=" + username);
		return user;
	}

	@Override
	public PageResponseModel<User> getByRole(String role, Integer page, Integer limit, String search, String field) {
		logger.info("Returned All customers");
		PageRequest pageReq = PageRequest.of(page, limit);
		PageResponseModel<User> userResponse = new PageResponseModel<User>();
		Page<User> user = null;
		if (search == null) {
			user = userRepo.findByRole(Role.valueOf(role.toUpperCase()), pageReq);
		} else {
			switch (field) {
			case "name":
				user = userRepo.findByNameContainingAndRole(search, Role.valueOf(role.toUpperCase()), pageReq);
				break;
			case "email":
				user = userRepo.findByEmailContainingAndRole(search, Role.valueOf(role.toUpperCase()), pageReq);
				break;
			case "username":
				user = userRepo.findByUsernameContainingAndRole(search, Role.valueOf(role.toUpperCase()), pageReq);
				break;
			default:
				user = userRepo.findByRole(Role.valueOf(role.toUpperCase()), pageReq);
			}
		}
		userResponse.setData(user.toList());
		userResponse.setCurrentPage(user.getNumber());
		userResponse.setLimit(limit);
		userResponse.setTotal(user.getTotalElements());
		userResponse.setTotalPages(user.getTotalPages());
		return userResponse;
	}

	@Override
	public User getUserById(long id) {
		Optional<User> user = userRepo.findById(id);
		if (user.isEmpty()) {
			throw new NotFoundException("User with id " + id + " not found");
		}
		logger.info("Returned user with userId=" + id);
		return user.get();
	}

	@Override
	public User updateUser(User user, long customerId, String updatedBy) {
		if (customerId == 0) {
			throw new NotFoundException("User id is not present");
		}
		Optional<User> userData = userRepo.findById(customerId);
		if (userData.isEmpty()) {
			throw new NotFoundException("User with id " + customerId + " not found");
		}
		User userInDb = userData.get();
		if (user.getName() != null)
			userInDb.setName(user.getName());
		if (user.getEmail() != null)
			userInDb.setEmail(user.getEmail());
		if (user.getUsername() != null)
			userInDb.setUsername(user.getUsername());
		if (user.getRole() != null)
			userInDb.setRole(user.getRole());
		userInDb.setModifiedTime(new Date());
		if (!updatedBy.equals(userInDb.getUsername()))
			userInDb.setModifiedBy(userRepo.findByUsername(updatedBy));
		userRepo.save(userInDb);
		logger.info("Updated user with userId=" + customerId);
		return userInDb;
	}

	@Override
	public void deleteUser(long id) {
		Optional<User> user = userRepo.findById(id);
		if (user.isEmpty()) {
			throw new NotFoundException("User with id " + id + " not found");
		}
		User userData = user.get();
		userData.setAccountActive(false);
		logger.info("Deleted user with userId=" + id);
		userRepo.save(userData);
	}

	@Override
	public void verifyEmail(String token) {
		System.out.println("Token: " + token);
		String username = new String(Base64.getUrlDecoder().decode(token.getBytes()));
		System.out.println("Username: " + username);
		User user = userRepo.findByUsername(username);
		if (user == null)
			throw new NotFoundException("Invalid URL or user not registered");
		user.setEmailVerified(true);
		userRepo.save(user);
	}

}
