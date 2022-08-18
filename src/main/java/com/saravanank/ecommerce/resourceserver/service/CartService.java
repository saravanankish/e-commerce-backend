package com.saravanank.ecommerce.resourceserver.service;

import com.saravanank.ecommerce.resourceserver.model.Cart;
import com.saravanank.ecommerce.resourceserver.model.ProductQuantityMapper;
import com.saravanank.ecommerce.resourceserver.model.User;

public interface CartService {

	public Cart getUserCart(String username);

	public Cart addProductsToCart(String username, ProductQuantityMapper product);

	public Cart addCartToUser(User user);

}
