package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;

@SpringBootTest
public class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepo;

	@Mock
	private UserService userService;

	@InjectMocks
	private CategoryService categoryService;

	Category category1 = new Category(1, "Category 1", new ArrayList<String>(), null, null, null);
	Category category2 = new Category(2, "Category 2", new ArrayList<String>(), null, null, null);
	Category category3 = new Category(3, "Category 2", new ArrayList<String>(), null, null, null);
	Category category4 = new Category(4, "Category 2", new ArrayList<String>(), null, null, null);

	User testUser1 = new User(1, "Test1", "test@gmail.com", "test1", "test", Role.ADMIN, false, null, null, null, null,
			null);

	@Test
	public void getById_success() {
		when(categoryRepo.findById(category1.getId())).thenReturn(Optional.of(category1));
		when(categoryRepo.findById(category2.getId())).thenReturn(Optional.of(category2));
		when(categoryRepo.findById(category3.getId())).thenReturn(Optional.of(category3));

		assertEquals(category1, categoryService.getById(1));
		assertEquals(category2, categoryService.getById(2));
		assertEquals(category3, categoryService.getById(3));
	}

	@Test
	public void getById_throwException() {
		when(categoryRepo.findById(5L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> categoryService.getById(4));
	}

	@Test
	public void getAll_success() {
		when(categoryRepo.findAll()).thenReturn(List.of(category1, category2, category3, category4));

		assertEquals(List.of(category1, category2, category3, category4), categoryService.getAll());
	}

	@Test
	public void addAll_success() {
		when(userService.getUserByUsername("test1")).thenReturn(testUser1);

		assertEquals(List.of(category1, category2, category3),
				categoryService.addAll(List.of(category1, category2, category3), "test1"));
		verify(categoryRepo, times(1)).saveAllAndFlush(List.of(category1, category2, category3));
	}

	@Test
	public void addAll_throwException() {
		when(userService.getUserByUsername("unknown")).thenThrow(NotFoundException.class);

		assertThrows(NotFoundException.class,
				() -> categoryService.addAll(List.of(category1, category2, category3), "unknown"));
	}
	
	@Test
	public void add_succes() throws JsonProcessingException, IllegalArgumentException {
		when(categoryRepo.save(category4)).thenReturn(category4);
		when(userService.getUserByUsername("test1")).thenReturn(testUser1);

		assertEquals(category4, categoryService.add(category4, "test1"));
	}

	@Test
	public void add_throwException() {
		when(userService.getUserByUsername("unknown")).thenThrow(NotFoundException.class);
		
		assertThrows(NotFoundException.class, () -> categoryService.add(category1, "unknown"));
	}

	@Test
	public void delete_success() {
		when(categoryRepo.existsById(3L)).thenReturn(true);
		categoryService.delete(3L);
		verify(categoryRepo, times(1)).deleteById(3L);
	}
	
	@Test
	public void delete_throwException() {
		when(categoryRepo.existsById(5L)).thenReturn(false);
		
		assertThrows(NotFoundException.class, () -> categoryService.delete(5L));
	}
	
}
