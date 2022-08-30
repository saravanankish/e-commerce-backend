package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@SpringBootTest
public class SubCategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepo;

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private SubCategoryServiceImpl subCategoryService;

	Date currentDate = new Date();
	Category category1 = new Category(1, "category 1", new ArrayList<>(List.of("sub-1-1", "sub-1-2", "sub-1-3")),
			currentDate, currentDate, null);
	Category category2 = new Category(2, "category 2", new ArrayList<>(List.of("sub-2-1", "sub-2-2")), currentDate,
			currentDate, null);
	Category category3 = new Category(3, "category 3", new ArrayList<>(List.of("sub-3-1")), currentDate, currentDate,
			null);
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.ADMIN, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);

	@Test
	public void getAllInId_success() {
		when(categoryRepo.findById(category2.getId())).thenReturn(Optional.of(category2));

		assertEquals(category2.getSubCategory(), subCategoryService.getAllInId(2));
	}

	@Test
	public void getAllInId_throwException() {
		when(categoryRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> subCategoryService.getAllInId(4), "Category with id 4 not found");
	}

	@Test
	public void getAll_success() {
		when(categoryRepo.findAllSubcategories())
				.thenReturn(List.of("sub-1-1", "sub-1-2", "sub-1-3", "sub-2-1", "sub-2-2", "sub-3-1"));

		assertEquals(List.of("sub-1-1", "sub-1-2", "sub-1-3", "sub-2-1", "sub-2-2", "sub-3-1"),
				subCategoryService.getAll());
	}

	@Test
	public void addAllToId_success() {
		when(categoryRepo.findById(3L)).thenReturn(Optional.of(category3));
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);

		assertEquals(List.of("sub-3-1", "sub-3-2"),
				subCategoryService.addAllToId(new HashSet<>(Set.of("sub-3-1", "sub-3-2")), "user1", 3));
	}

	@Test
	public void addAllToIdEmpty_throwException() {
		when(categoryRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);

		assertThrows(BadRequestException.class, () -> subCategoryService.addAllToId(Set.of(), "user1", 1),
				"Data is empty");
		assertThrows(BadRequestException.class,
				() -> subCategoryService.addAllToId(new HashSet<>(Set.of("", "test")), "user1", 1),
				"One or more of subcategory is empty");
	}

	@Test
	public void addAllToIdCategoryNotFound_throwException() {
		when(categoryRepo.findById(3L)).thenReturn(Optional.of(category3));
		when(categoryRepo.findById(4L)).thenReturn(Optional.empty());
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);
		when(userRepo.findByUsername("user2")).thenReturn(null);

		assertThrows(NotFoundException.class, () -> subCategoryService.addAllToId(Set.of("Test"), "user1", 4),
				"Category with id 4 not found");
		assertThrows(NotFoundException.class, () -> subCategoryService.addAllToId(Set.of("Test"), "user2", 3),
				"User with username user2 not found");
	}

	@Test
	public void add_success() {
		when(categoryRepo.findById(2L)).thenReturn(Optional.of(category2));
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);

		assertEquals(List.of("sub-2-1", "sub-2-2", "sub-2-3"),
				subCategoryService.addAllToId(new HashSet<>(Set.of("sub-2-1", "sub-2-3")), "user1", 2));
	}

	@Test
	public void add_throwException() {
		when(categoryRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepo.findById(4L)).thenReturn(Optional.empty());
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);
		when(userRepo.findByUsername("user2")).thenReturn(null);

		assertThrows(BadRequestException.class, () -> subCategoryService.add("", "user1", 1), "Subcategory is empty");
		assertThrows(BadRequestException.class, () -> subCategoryService.add(null, "user1", 1), "Subcategory is empty");
		assertThrows(NotFoundException.class, () -> subCategoryService.add("test", "user1", 4),
				"Category with id 4 not found");
		assertThrows(NotFoundException.class, () -> subCategoryService.add("test", "user2", 1),
				"User with username user2 not found");
	}

	@Test
	public void update_success() {
		when(categoryRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);

		assertEquals("sub-1--3", subCategoryService.update("sub-1--3", 2, "user1", 1));
	}

	@Test
	public void update_throwException() {
		when(categoryRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepo.findById(4L)).thenReturn(Optional.empty());
		when(userRepo.findByUsername("user1")).thenReturn(testUser1);
		when(userRepo.findByUsername("user2")).thenReturn(null);

		assertThrows(BadRequestException.class, () -> subCategoryService.update("", 3, "user1", 1),
				"Subcategory is empty");
		assertThrows(BadRequestException.class, () -> subCategoryService.update(null, 3, "user1", 1),
				"Subcategory is empty");
		assertThrows(BadRequestException.class, () -> subCategoryService.update("test1", 5, "user1", 1),
				"Index to update is not present");
		assertThrows(NotFoundException.class, () -> subCategoryService.add("test", "user1", 4),
				"Category with id 4 not found");
		assertThrows(NotFoundException.class, () -> subCategoryService.add("test", "user2", 1),
				"User with username user2 not found");
	}
	
	@Test
	public void delete_success() {
		when(categoryRepo.findById(2L)).thenReturn(Optional.of(category2));
		
		subCategoryService.delete(1, 2);
		assertEquals(1, category2.getSubCategory().size());
	}
	
	@Test
	public void delete_throwException() {
		when(categoryRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepo.findById(4L)).thenReturn(Optional.empty());
	
		assertThrows(NotFoundException.class, () -> subCategoryService.delete(1, 4), "Category with id 4 not found");
		assertThrows(BadRequestException.class, () -> subCategoryService.delete(4, 1), "Index to delete is not present");
	}
}
