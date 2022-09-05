package com.saravanank.ecommerce.resourceserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	public User findByUsername(String username);

	public Page<User> findByRole(Role role, PageRequest request);

	public Page<User> findByNameContainingAndRole(String name, Role role, PageRequest request);

	public Page<User> findByEmailContainingAndRole(String email, Role role, PageRequest request);

	public Page<User> findByUsernameContainingAndRole(String username, Role role, PageRequest request);

	public boolean existsByUsername(String username);

}
