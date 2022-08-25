package com.saravanank.ecommerce.resourceserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.saravanank.ecommerce.resourceserver.model.Address;
import com.saravanank.ecommerce.resourceserver.model.Cart;
import com.saravanank.ecommerce.resourceserver.model.DeliveryTimingType;
import com.saravanank.ecommerce.resourceserver.model.Json;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.CartRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@SpringBootTest
public class CartServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private CartRepository cartRepo;

	@Mock
	private ProductRepository productRepo;

	@InjectMocks
	private CartServiceImpl cartService;

	Address address1 = new Address(1, "19", "Street 1", "Area 1", "City 1", "State 1", 600001, "Landmark 1", "Test 1",
			true, DeliveryTimingType.HOME, null);
	Address address2 = new Address(2, "11", "Street 2", "Area 2", "City 2", "State 2", 600002, "Landmark 2", "Test 2",
			false, DeliveryTimingType.OFFICE, null);
	User testUser1 = new User(1, "test1", "test1", "test1", "test1", "test1", true, null, null, null, null,
			new ArrayList<Address>(List.of(address1, address2)));
	User testUser2 = new User(1, "test2", "test2", "test2", "test2", "test2", true, null, null, null, null,
			new ArrayList<Address>(List.of(address1, address2)));
	Cart user1Cart = new Cart(0, null, testUser1);
	Cart user2Cart = new Cart(0, null, null);
	Product product1 = new Product(1, "Product1", "Description", 100, 100, 5, null, null, null, null, null, null,
			testUser1);
	Product product2 = new Product(1, "Product2", "Description", 500, 200, 4, null, null, null, null, null, null,
			testUser1);
	ProductQuantityMapper mapper1 = new ProductQuantityMapper(0, product1, 10);
	ProductQuantityMapper mapper2 = new ProductQuantityMapper(0, product2, 5);

	@Test
	public void getUserCart_success() {
		when(userRepo.existsByUsername(testUser1.getUsername())).thenReturn(true);
		when(cartRepo.findByUserUsername(testUser1.getUsername())).thenReturn(user1Cart);

		assertEquals(user1Cart, cartService.getUserCart("test1"));
	}

	@Test
	public void getUserCart_throwException() {
		when(userRepo.existsByUsername("invalid")).thenReturn(false);

		assertThrows(NotFoundException.class, () -> cartService.getUserCart("invalid"));
	}

	@Test
	public void addCartToUser_success() throws JsonProcessingException, IllegalArgumentException {
		when(cartRepo.save(user2Cart)).thenReturn(user2Cart);

		Cart responseCart = Json.fromJson(Json.toJson(user2Cart), Cart.class);
		responseCart.setUser(testUser2);

		assertEquals(responseCart, cartService.addCartToUser(testUser2));
	}

	@Test
	public void addCartToUser_throwException() throws JsonProcessingException, IllegalArgumentException {
		User tempUser = Json.fromJson(Json.toJson(testUser1), User.class);
		tempUser.setUserId(0);
		assertThrows(NotFoundException.class, () -> cartService.addCartToUser(tempUser));
	}

	@Test
	public void addProductToCart_success() throws JsonProcessingException, IllegalArgumentException {
		Cart responseCart = Json.fromJson(Json.toJson(user1Cart), Cart.class);
		responseCart.setProducts(new ArrayList<ProductQuantityMapper>(List.of(mapper1)));

		// First product
		when(userRepo.existsByUsername(testUser1.getUsername())).thenReturn(true);
		when(productRepo.findById(product1.getProductId())).thenReturn(Optional.of(product1));
		when(cartRepo.findByUserUsername(testUser1.getUsername())).thenReturn(user1Cart);
		when(cartRepo.saveAndFlush(responseCart)).thenReturn(responseCart);

		assertEquals(responseCart, cartService.addProductsToCart("test1", mapper1));

		// Second product
		when(cartRepo.findByUserUsername(testUser1.getUsername())).thenReturn(responseCart);
		responseCart.getProducts().add(mapper2);

		when(cartRepo.saveAndFlush(responseCart)).thenReturn(responseCart);

		assertEquals(responseCart, cartService.addProductsToCart("test1", mapper2));

		// When product with zero quantity is passed
		when(cartRepo.findByUserUsername(testUser1.getUsername())).thenReturn(responseCart);
		responseCart.getProducts().remove(mapper1);

		ProductQuantityMapper tempMapper = Json.fromJson(Json.toJson(mapper1), ProductQuantityMapper.class);
		tempMapper.setQuantity(0);
		assertEquals(responseCart, cartService.addProductsToCart("test1", tempMapper));
	}

	@Test
	public void addProductToCart_userNotFoundException() {
		when(userRepo.existsByUsername("Invalid")).thenReturn(false);
		
		assertThrows(NotFoundException.class, () -> cartService.addProductsToCart("Invalid", mapper1));
	}
	
	@Test
	public void addProductsToCart_productNotFoundException() throws JsonProcessingException, IllegalArgumentException {
		when(userRepo.existsByUsername(testUser1.getUsername())).thenReturn(true);
		when(productRepo.findById(3L)).thenReturn(Optional.empty());
		
		ProductQuantityMapper tempMapper = Json.fromJson(Json.toJson(mapper1), ProductQuantityMapper.class);
		tempMapper.getProduct().setProductId(3);
		assertThrows(NotFoundException.class, () -> cartService.addProductsToCart("test1", mapper1));
	}
	
	@Test
	public void addProductsToCart_cartNotFoundException() {
		when(userRepo.existsByUsername(testUser2.getUsername())).thenReturn(true);
		when(productRepo.findById(product1.getProductId())).thenReturn(Optional.of(product1));
		when(cartRepo.findByUserUsername(testUser2.getUsername())).thenReturn(null);
		
		assertThrows(NotFoundException.class,() -> cartService.addProductsToCart("test2", mapper1));
	}
	
}
