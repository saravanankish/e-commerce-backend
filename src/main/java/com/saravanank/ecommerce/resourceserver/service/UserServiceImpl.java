package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("No user found");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true,
				true, true, true, getAuthorities(Arrays.asList(user.getRole())));
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
	public User addUser(User user) {
		if (userRepo.existsByUsername(user.getUsername())) {
			throw new BadRequestException("Username already present");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreationTime(new Date());
		user.setModifiedTime(new Date());
		userRepo.save(user);
		if (user.getRole().equals("CUSTOMER"))
			cartService.addCartToUser(user);
		logger.info("Added user with userid=" + user.getUserId());
		return user;
	}

	@Override
	public User getUserByUsername(String username) {
		logger.info("Returned user with username=" + username);
		return userRepo.findByUsername(username);
	}

	@Override
	public PageResponseModel<User> getByRole(String role, Integer page, Integer limit, String search) {
		logger.info("Returned All customers");
		PageRequest pageReq = PageRequest.of(page, limit);
		PageResponseModel<User> userResponse = new PageResponseModel<User>();
		Page<User> user;
		if (search == null) {
			user = userRepo.findByRole(role.toUpperCase(), pageReq);
		} else {
			user = userRepo.findByRoleAndNameContainingOrEmailContainingOrUsernameContaining(role.toUpperCase(), search,
					search, search, pageReq);
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
			throw new NotFoundException("User with id is not present");
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

}
