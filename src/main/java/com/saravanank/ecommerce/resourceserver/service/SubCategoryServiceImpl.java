package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Category;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.CategoryRepository;
import com.saravanank.ecommerce.resourceserver.repository.UserRepository;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	private static final Logger logger = Logger.getLogger(SubCategoryServiceImpl.class);

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public List<String> getAllInId(long id) {
		logger.info("Returned all subCategories of category with id " + id);
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty())
			throw new NotFoundException("Category with id " + id + " not found");
		return categoryInDb.get().getSubCategory();
	}

	@Override
	public List<String> getAll() {
		logger.info("Returned all subCategories");
		return categoryRepo.findAllSubcategories();
	}

	@Override
	public List<String> addAllToId(Set<String> data, String modifiedBy, long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		User user = userRepo.findByUsername(modifiedBy);
		if (categoryInDb.isEmpty())
			throw new NotFoundException("Category with id " + id + " not found");
		if (user == null)
			throw new NotFoundException("User with username " + modifiedBy + " not found");
		Category category = categoryInDb.get();
		data.removeIf(d -> category.getSubCategory().contains(d));
		category.getSubCategory().addAll(data);
		category.setModifiedBy(user);
		category.setModifiedDate(new Date());
		categoryRepo.saveAndFlush(category);
		return category.getSubCategory();
	}

	@Override
	public String add(String data, String modifiedBy, long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if(categoryInDb.isEmpty()) throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		if(category.getSubCategory().contains(data))
			throw new BadRequestException("Subcategory already exists in the category");
		category.getSubCategory().add(data);
		categoryRepo.saveAndFlush(category);
		return data;
	}

	@Override
	public String update(String data, int index, String modifiedBy, long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if(categoryInDb.isEmpty()) throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		category.getSubCategory().set(index, data);
		categoryRepo.saveAndFlush(category);
		return data;
	}

	@Override
	public void delete(int index, long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if(categoryInDb.isEmpty()) throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		category.getSubCategory().remove(index);
		categoryRepo.saveAndFlush(category);
	}

}
