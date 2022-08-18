package com.saravanank.ecommerce.resourceserver.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;

@Service
public class CategoryService implements CrudOperationService<Category> {

	private static final Logger logger = Logger.getLogger(CategoryService.class);

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private UserService userService;

	@Override
	public Category getById(long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty()) {
			throw new NotFoundException("Category with id " + id + " not found");
		}
		logger.info("Returned category with id=" + id);
		return categoryInDb.get();
	}

	@Override
	public List<Category> getAll() {
		logger.info("Returned all categories");
		return categoryRepo.findAll();
	}

	@Override
	public List<Category> addAll(List<Category> data, String modifiedBy) {
		User modifiedByUser = userService.getUserByUsername(modifiedBy);
		data.stream().forEach(category -> {
			category.setModifiedBy(modifiedByUser);
			category.setCreationDate(new Date(new java.util.Date().getTime()));
			category.setModifiedDate(new Date(new java.util.Date().getTime()));
		});
		logger.info("Added " + data.size() + " category(ies)");
		categoryRepo.saveAllAndFlush(data);
		return data;
	}

	@Override
	public Category add(Category data, String modifiedBy) {
		data.setCreationDate(new Date(new java.util.Date().getTime()));
		data.setModifiedDate(new Date(new java.util.Date().getTime()));
		data.setModifiedBy(userService.getUserByUsername(modifiedBy));
		categoryRepo.saveAndFlush(data);
		logger.info("Added category with category_id=" + data.getId());
		return data;
	}

	@Override
	public Category update(Category data, long id, String modifiedBy) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty()) {
			throw new NotFoundException("Category with id " + id + " not found");
		}
		Category categoryData = categoryInDb.get();
		if (data.getName() != null)
			categoryData.setName(data.getName());
		if (data.getSubCategory() != null)
			categoryData.setSubCategory(data.getSubCategory());
		categoryData.setModifiedDate( new Date(new java.util.Date().getTime()));
		categoryData.setModifiedBy(userService.getUserByUsername(modifiedBy));
		categoryRepo.saveAndFlush(categoryData);
		logger.info("Updated category with id=" + id);
		return categoryData;
	}

	@Override
	public void delete(long id) {
		boolean categoryPresent = categoryRepo.existsById(id);
		if (!categoryPresent) {
			throw new NotFoundException("Category with id " + id + " not found");
		}
		logger.info("Deleted category with id=" + id);
		categoryRepo.deleteById(id);
	}

}
