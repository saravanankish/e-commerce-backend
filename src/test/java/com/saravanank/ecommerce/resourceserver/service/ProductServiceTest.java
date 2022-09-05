package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Brand;
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.MobileNumber;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.Role;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.BrandRepository;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;

@SpringBootTest
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepo;

	@Mock
	private UserService userService;

	@Mock
	private BrandRepository brandRepo;

	@Mock
	private CategoryRepository categoryRepo;

	@InjectMocks
	private ProductService prodService;

	Date currentDate = new Date();
	User testUser1 = new User(1, "User 1", "user1@gmail.com", "user1", "user1", Role.ADMIN, true, true, currentDate,
			currentDate, new ArrayList<MobileNumber>(), null, null);
	Brand brand1 = new Brand(1, "brand1", currentDate, currentDate, testUser1);
	Brand brand2 = new Brand(2, "brand2", currentDate, currentDate, testUser1);
	Brand tempBrand2 = new Brand(0, "brand2", null, null, null);
	Brand tempBrand1 = new Brand(0, "brand1", null, null, null);
	Product prod1 = new Product(1, "Product 1", "desc 1", 10, 100, 4, "thumbnail1", null, brand1, "sub1", currentDate,
			currentDate, testUser1);
	Product prod2 = new Product(2, "Product 2", "desc 2", 40, 50, 4, "thumbnail2", null, tempBrand2, "sub2",
			currentDate, currentDate, testUser1);
	Product prod3 = new Product(3, "Product 3", "desc 3", 100, 93, 4, "thumbnail3", null, brand2, "sub2", currentDate,
			currentDate, testUser1);
	Product tempProd1 = new Product(1, "Product 1", "desc 1", 10, 100, 4, "thumbnail1", null, tempBrand1, "sub1",
			currentDate, currentDate, testUser1);
	Product tempProd2 = new Product(2, "Product 2", "desc 2", 40, 50, 4, "thumbnail2", null, tempBrand2, "sub2",
			currentDate, currentDate, testUser1);

	@Test
	public void getById_success() {
		when(productRepo.findById(2L)).thenReturn(Optional.of(prod2));

		assertEquals(prod2, prodService.getById(2));
	}

	@Test
	public void getById_throwException() {
		when(productRepo.findById(4L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> prodService.getById(4), "Product with id 4 not found");
	}

	@Test
	public void delete_success() {
		when(productRepo.existsById(1L)).thenReturn(true);

		prodService.delete(1);
		verify(productRepo, times(1)).deleteById(1L);
	}

	@Test
	public void delete_throwException() {
		when(productRepo.existsById(4L)).thenReturn(false);

		assertThrows(NotFoundException.class, () -> prodService.getById(4), "Product with id 4 not found");
	}

	@Test
	public void add_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand1")).thenReturn(brand1);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(true);
		when(productRepo.save(prod1)).thenReturn(prod1);

		assertEquals(prod1, prodService.add(prod1, "user1"));
	}

	@Test
	public void add_addNewBrand_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand2")).thenReturn(null);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(true);

		prodService.add(prod2, "user1");
		ArgumentCaptor<Brand> argument = ArgumentCaptor.forClass(Brand.class);
		verify(brandRepo).saveAndFlush(argument.capture());
	}

	@Test
	public void add_addNewCategory_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand2")).thenReturn(null);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(false);

		prodService.add(prod2, "user1");
		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);
		verify(categoryRepo).saveAndFlush(argument.capture());
	}

	@Test
	public void addAll_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand1")).thenReturn(brand1);
		when(brandRepo.findByNameIgnoreCase("brand2")).thenReturn(brand1);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(true);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub2")).thenReturn(true);
		when(productRepo.saveAll(List.of(prod1, prod3))).thenReturn(List.of(prod1, prod3));

		assertEquals(List.of(prod1, prod3), prodService.addAll(List.of(prod1, prod3), "user1"));
	}

	@Test
	public void addAll_newBrand_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand1")).thenReturn(null);
		when(brandRepo.findByNameIgnoreCase("brand2")).thenReturn(null);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(true);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub2")).thenReturn(true);

		prodService.addAll(List.of(tempProd1, tempProd2), "user1");
		ArgumentCaptor<Brand> argument = ArgumentCaptor.forClass(Brand.class);
		verify(brandRepo, times(2)).saveAndFlush(argument.capture());
	}

	@Test
	public void addAll_newCategory_success() {
		when(userService.getUserByUsername("user1")).thenReturn(testUser1);
		when(brandRepo.findByNameIgnoreCase("brand1")).thenReturn(brand1);
		when(brandRepo.findByNameIgnoreCase("brand2")).thenReturn(brand2);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub1")).thenReturn(false);
		when(categoryRepo.existsBySubCategoryIgnoreCase("sub2")).thenReturn(false);

		prodService.addAll(List.of(prod1, prod2), "user1");
		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);
		verify(categoryRepo, times(2)).saveAndFlush(argument.capture());
	}

}
