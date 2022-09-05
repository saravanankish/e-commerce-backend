package com.saravanank.ecommerce.resourceserver.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.User;

public interface UserService extends UserDetailsService {

	public User addUser(User user);
	
	public User getUserByUsername(String username);
	
	public PageResponseModel<User> getByRole(String role, Integer page, Integer limit, String search, String field);
	
	public User getUserById(long id);
	
	public User updateUser(User user, long customerId, String updatedBy);
	
	public void deleteUser(long id);
	
}
