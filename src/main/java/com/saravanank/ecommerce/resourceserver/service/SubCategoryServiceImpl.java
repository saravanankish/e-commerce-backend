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
import com.saravanank.ecommerce.resourceserver.model.OptionValue;
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
	public List<String> getAll(String search) {
		logger.info("Returned all subCategories");
		return categoryRepo.findAllSubcategories();
	}

	@Override
	public List<String> addAllToId(Set<String> data, String modifiedBy, long id) {
		if (data.size() == 0) {
			throw new BadRequestException("Data is empty");
		}
		if (data.stream().anyMatch(d -> d.equals(""))) {
			throw new BadRequestException("One or more of subcategory is empty");
		}
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
		if (data == null || data.equals(""))
			throw new BadRequestException("Subcategory is empty");
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty())
			throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		if (category.getSubCategory().contains(data))
			throw new BadRequestException("Subcategory already exists in the category");
		User user = userRepo.findByUsername(modifiedBy);
		if (user == null)
			throw new NotFoundException("User with username " + modifiedBy + " not found");
		category.setModifiedBy(user);
		category.setModifiedDate(new Date());
		category.getSubCategory().add(data);
		categoryRepo.saveAndFlush(category);
		return data;
	}

	@Override
	public String update(String data, int index, String modifiedBy, long id) {
		if (data == null || data.equals(""))
			throw new BadRequestException("Subcategory is empty");
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty())
			throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		if(category.getSubCategory().size() < index)
			throw new BadRequestException("Index to update is not present");
		category.getSubCategory().set(index, data);
		User user = userRepo.findByUsername(modifiedBy);
		if (user == null)
			throw new NotFoundException("User with username " + modifiedBy + " not found");
		category.setModifiedBy(user);
		category.setModifiedDate(new Date());
		categoryRepo.saveAndFlush(category);
		return data;
	}

	@Override
	public void delete(int index, long id) {
		Optional<Category> categoryInDb = categoryRepo.findById(id);
		if (categoryInDb.isEmpty())
			throw new NotFoundException("Category with id " + id + " not found");
		Category category = categoryInDb.get();
		if(category.getSubCategory().size() < index)
			throw new BadRequestException("Index to delete is not present");
		category.getSubCategory().remove(index);
		categoryRepo.saveAndFlush(category);
	}

	@Override
	public List<OptionValue> getAllForOption() {
		return null;
	}

}
