package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.saravanank.ecommerce.resourceserver.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	public User findByUsername(String username);

	public Page<User> findByRole(String role, PageRequest request);

	public Page<User> findByNameContainingOrEmailContainingOrUsernameContainingAndRole(String name,
			String email, String username, String role, PageRequest request);

	public boolean existsByUsername(String username);

}
