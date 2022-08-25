package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Brand;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.BrandRepository;

@SpringBootTest
public class BrandServiceTest {

	@Mock
	private BrandRepository brandRepo;
	
	@Mock
	private UserServiceImpl userService;

	@InjectMocks
	private BrandService brandService;

	User testUser = new User(1, "test", "test", "test", "test", "test", true, null, null, null, null, null);

	Date currentDate = new Date();
	Brand brand1 = new Brand(1, "Apple", currentDate, currentDate, testUser);
	Brand brand2 = new Brand(2, "Samsung", currentDate, currentDate, testUser);
	Brand brand3 = new Brand(3, "Redmi", currentDate, currentDate, testUser);
	Brand brand4 = new Brand(4, "Vivo", currentDate, currentDate, testUser);
	Brand newBrand = new Brand(5, "Nokia", currentDate, currentDate, testUser);
	Brand newBrandUpdate = new Brand(5, "Nokia1", null, null, null);
	Brand updatedNewBrand = new Brand(5, "Nokia1", currentDate, new Date(), testUser);
	
	@Test
	public void getAllBrands_success() throws Exception {
		List<Brand> brands = List.of(brand1, brand2, brand3, brand4);
		when(brandRepo.findAll()).thenReturn(brands);

		List<Brand> getResult = brandService.getAll();
		assertEquals(brands, getResult);
	}

	@Test
	public void getBrandById_success() throws Exception {		
		when(brandRepo.findById(2L)).thenReturn(Optional.of(brand2));
		when(brandRepo.findById(4L)).thenReturn(Optional.of(brand4));
		
		Brand getByIdResponse = brandService.getById(2);
		Brand getByIdResponse2 = brandService.getById(4);
		assertEquals(brand2, getByIdResponse);
		assertEquals(brand4, getByIdResponse2);
	}
	
	@Test
	public void getBrandById_throwException() {		
		when(brandRepo.findById(1L)).thenReturn(Optional.of(brand1));
		when(brandRepo.findById(2L)).thenReturn(Optional.of(brand2));
		when(brandRepo.findById(3L)).thenReturn(Optional.of(brand3));
		when(brandRepo.findById(4L)).thenReturn(Optional.of(brand4));
		assertThrows(NotFoundException.class, () -> brandService.getById(20));
	}
	
	@Test
	public void addBrand_success() {
		when(brandRepo.save(newBrand)).thenReturn(newBrand);
		when(userService.getUserByUsername("test")).thenReturn(testUser);

		assertEquals(newBrand, brandService.add(newBrand, "test"));
	}

	@Test
	public void addAllBrand_success() {
		List<Brand> brands = List.of(brand1, brand2, brand3, brand4);
		when(brandRepo.saveAll(brands)).thenReturn(brands);
		when(userService.getUserByUsername("test")).thenReturn(testUser);
		
		assertEquals(brands, brandService.addAll(brands, "test"));
	}
	
	@Test
	public void updateBrand_success() {
		when(brandRepo.save(newBrandUpdate)).thenReturn(updatedNewBrand);
		when(userService.getUserByUsername("test")).thenReturn(testUser);
		when(brandRepo.findById(5L)).thenReturn(Optional.of(updatedNewBrand));
		
		assertEquals(updatedNewBrand, brandService.update(newBrandUpdate, 5, "test"));
	}
	
	@Test
	public void updateBrand_throwException() {
		when(userService.getUserByUsername("test")).thenReturn(testUser);
		
		assertThrows(NotFoundException.class, () -> brandService.update(newBrandUpdate, 5, "test"));
	}
	
	@Test
	public void deleteBrand_success() {
		when(brandRepo.existsById(3L)).thenReturn(true);
		
		brandService.delete(3L);
		verify(brandRepo, times(1)).deleteById(3L);
	}

	@Test
	public void deleteBrand_throwException() {
		assertThrows(NotFoundException.class, () -> brandService.delete(3L));
	}
	
}
