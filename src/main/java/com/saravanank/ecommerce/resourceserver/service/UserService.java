package com.saravanank.ecommerce.resourceserver.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.User;

public interface UserService extends UserDetailsService {

	public User addUser(User user) throws JsonProcessingException;
	
	public User getUserByUsername(String username);
	
	public PageResponseModel<User> getByRole(String role, Integer page, Integer limit, String search, String field);
	
	public User getUserById(long id);
	
	public User updateUser(User user, long customerId, String updatedBy);
	
	public void deleteUser(long id);
	
	public void verifyEmail(String token);
	
}
