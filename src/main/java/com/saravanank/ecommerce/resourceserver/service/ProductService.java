package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Product;
import com.saravanank.ecommerce.resourceserver.model.Brand;
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.PageResponseModel;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.BrandRepository;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;
import com.saravanank.ecommerce.resourceserver.repository.ProductRepository;

@Service
public class ProductService implements PageCrudOperationService<Product, PageResponseModel<Product>> {

	private static final Logger logger = Logger.getLogger(ProductService.class);

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private CategoryRepository categoryRepo;

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
			if (product.getBrand().getId() == 0) {
				Brand brandInDb = brandRepo.findByNameIgnoreCase(product.getBrand().getName());
				if (brandInDb != null) {
					product.setBrand(brandInDb);
				} else {
					Brand newBrand = new Brand();
					newBrand.setName(product.getBrand().getName());
					newBrand.setCreationDate(new Date());
					newBrand.setModifiedBy(modifiedByUser);
					newBrand.setModifiedDate(new Date());
					brandRepo.saveAndFlush(newBrand);
					product.setBrand(newBrand);
				}
			}
			if (!categoryRepo.existsBySubCategoryIgnoreCase(product.getCategory())) {
				Category category = categoryRepo.findByNameIgnoreCase("Others");
				if (category == null) {
					Category newCategory = new Category();
					newCategory.setCreationDate(new Date());
					newCategory.setModifiedDate(new Date());
					newCategory.setModifiedBy(modifiedByUser);
					newCategory.setName("Others");
					newCategory.setSubCategory(List.of(product.getCategory()));
					categoryRepo.saveAndFlush(newCategory);
				} else {
					category.getSubCategory().add(product.getCategory());
					categoryRepo.saveAndFlush(category);
				}
			}
			product.setCreationDate(new Date(new java.util.Date().getTime()));
			product.setModifiedDate(new Date(new java.util.Date().getTime()));
			product.setModifiedBy(modifiedByUser);
		});
		logger.info("Added " + data.size() + " product(s)");
		return (List<Product>) this.productRepo.saveAll(data);
	}

	@Override
	public Product add(Product data, String modifiedBy) {
		User modifiedByUser = userService.getUserByUsername(modifiedBy);
		if (data.getBrand().getId() == 0) {
			Brand brandInDb = brandRepo.findByNameIgnoreCase(data.getBrand().getName());
			if (brandInDb != null) {
				data.setBrand(brandInDb);
			} else {
				Brand newBrand = new Brand();
				newBrand.setName(data.getBrand().getName());
				newBrand.setCreationDate(new Date());
				newBrand.setModifiedBy(modifiedByUser);
				newBrand.setModifiedDate(new Date());
				brandRepo.saveAndFlush(newBrand);
				data.setBrand(newBrand);
			}
		}
		if (!categoryRepo.existsBySubCategoryIgnoreCase(data.getCategory())) {
			Category category = categoryRepo.findByNameIgnoreCase("Others");
			if (category == null) {
				Category newCategory = new Category();
				newCategory.setCreationDate(new Date());
				newCategory.setModifiedDate(new Date());
				newCategory.setModifiedBy(modifiedByUser);
				newCategory.setName("Others");
				newCategory.setSubCategory(List.of(data.getCategory()));
				categoryRepo.saveAndFlush(newCategory);
			} else {
				category.getSubCategory().add(data.getCategory());
				categoryRepo.saveAndFlush(category);
			}
		}
		data.setModifiedDate(new Date());
		data.setCreationDate(new Date());
		data.setModifiedBy(modifiedByUser);
		productRepo.save(data);
		logger.info("Added product with productId=" + data.getProductId());
		return data;
	}

	@Override
	public Product update(Product data, long id, String modifiedBy) {
		User modifiedByUser = userService.getUserByUsername(modifiedBy);
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
		if (data.getBrand() != null) {
			if (data.getBrand().getId() == 0) {
				Brand brandInDb = brandRepo.findByNameIgnoreCase(data.getBrand().getName());
				if (brandInDb != null) {
					data.setBrand(brandInDb);
				} else {
					Brand newBrand = new Brand();
					newBrand.setName(data.getBrand().getName());
					newBrand.setCreationDate(new Date());
					newBrand.setModifiedBy(modifiedByUser);
					newBrand.setModifiedDate(new Date());
					brandRepo.saveAndFlush(newBrand);
					data.setBrand(newBrand);
				}
			}
			productData.setBrand(data.getBrand());
		}
		if (data.getCategory() != null) {
			if (!categoryRepo.existsBySubCategoryIgnoreCase(data.getCategory())) {
				Category category = categoryRepo.findByNameIgnoreCase("Others");
				if (category == null) {
					Category newCategory = new Category();
					newCategory.setCreationDate(new Date());
					newCategory.setModifiedDate(new Date());
					newCategory.setModifiedBy(modifiedByUser);
					newCategory.setName("Others");
					newCategory.setSubCategory(List.of(data.getCategory()));
					categoryRepo.saveAndFlush(newCategory);
				} else {
					category.getSubCategory().add(data.getCategory());
					categoryRepo.saveAndFlush(category);
				}
			}
			productData.setCategory(data.getCategory());
		}
		data.setModifiedDate(new Date(new java.util.Date().getTime()));
		data.setModifiedBy(modifiedByUser);
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
	public PageResponseModel<Product> getAll(Integer page, Integer limit, String search) {
		PageRequest pageReq = PageRequest.of(page, limit);
		PageResponseModel<Product> productResponse = new PageResponseModel<Product>();
		Page<Product> products;
		if (search == null) {
			products = productRepo.findAll(pageReq);
		} else {
			products = productRepo.findByNameContainingOrDescriptionContaining(search, search, pageReq);
		}
		productResponse.setData(products.toList());
		productResponse.setTotal(products.getTotalElements());
		productResponse.setTotalPages(products.getTotalPages());
		productResponse.setCurrentPage(products.getNumber());
		productResponse.setLimit(limit);
		logger.info("Returned products");
		return productResponse;
	}

}
