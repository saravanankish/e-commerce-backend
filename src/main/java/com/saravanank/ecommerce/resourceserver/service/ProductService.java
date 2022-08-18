package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.ProductResponseModel;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;

@Service
public class ProductService implements PageCrudOperationService<Product, ProductResponseModel> {

	private static final Logger logger = Logger.getLogger(ProductService.class);

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private UserService userService;

	@Override
	public Product getById(long id) {
		Optional<Product> product = productRepo.findById(id);
		if (product.isEmpty())
			throw new NotFoundException("Product with id " + id + " not found");
		return product.get();
	}

	@Override
	public List<Product> addAll(List<Product> data, String modifiedBy) {
		User modifiedByUser = userService.getUserByUsername(modifiedBy);
		data.stream().forEach(product -> {
			product.setCreationDate(new Date(new java.util.Date().getTime()));
			product.setModifiedDate(new Date(new java.util.Date().getTime()));
			product.setModifiedBy(modifiedByUser);
		});
		logger.info("Added " + data.size() + " product(s)");
		return (List<Product>) this.productRepo.saveAll(data);
	}

	@Override
	public Product add(Product data, String modifiedBy) {
		data.setModifiedDate(new Date());
		data.setCreationDate(new Date());
		data.setModifiedBy(userService.getUserByUsername(modifiedBy));
		productRepo.save(data);
		logger.info("Added product with productId=" + data.getProductId());
		return data;
	}

	@Override
	public Product update(Product data, long id, String modifiedBy) {
		Optional<Product> productInDb = productRepo.findById(id);
		if (productInDb.isEmpty()) {
			throw new NotFoundException("Product with id " + id + " not found");
		}
		Product productData = productInDb.get();
		if (data.getName() != null)
			productData.setName(data.getName());
		if (data.getDescription() != null)
			productData.setDescription(data.getDescription());
		if (data.getPrice() != 0)
			productData.setPrice(data.getPrice());
		if (data.getQuantity() != null)
			productData.setQuantity(data.getQuantity());
		if (data.getRating() != 0)
			productData.setRating(data.getRating());
		if (data.getThumbnail() != null)
			productData.setThumbnail(data.getThumbnail());
		if (data.getImages() != null)
			productData.setImages(data.getImages());
		if (data.getBrand() != null)
			productData.setBrand(data.getBrand());
		if (data.getCategory() != null)
			productData.setCategory(data.getCategory());
		data.setModifiedDate(new Date(new java.util.Date().getTime()));
		data.setModifiedBy(userService.getUserByUsername(modifiedBy));
		productRepo.save(productData);
		logger.info("Updated product with productId=" + data.getProductId());
		return productData;
	}

	@Override
	public void delete(long id) {
		boolean productPresent = productRepo.existsById(id);
		if (!productPresent) {
			throw new NotFoundException("Product with id " + id + " not found");
		}
		logger.info("Deleted product with productId=" + id);
		productRepo.deleteById(id);
	}

	@Override
	public ProductResponseModel getAll(Integer page, Integer limit, String search) {
		PageRequest pageReq = PageRequest.of(page, limit);
		ProductResponseModel productResponse = new ProductResponseModel();
		Page<Product> products;
		if (search == null) {
			products = productRepo.findAll(pageReq);
		} else {
			products = productRepo.findByNameContainingOrDescriptionContaining(search, search, pageReq);
		}
		productResponse.setProducts(products.toList());
		productResponse.setTotal(products.getTotalElements());
		productResponse.setTotalPages(products.getTotalPages());
		productResponse.setCurrentPage(products.getNumber());
		productResponse.setLimit(limit);
		logger.info("Returned products");
		return productResponse;
	}

}
