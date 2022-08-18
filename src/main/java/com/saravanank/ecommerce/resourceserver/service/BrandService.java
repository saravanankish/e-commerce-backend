package com.saravanank.ecommerce.resourceserver.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.Brand;
import com.saravanank.ecommerce.resourceserver.model.User;
import com.saravanank.ecommerce.resourceserver.repository.BrandRepository;

@Service
public class BrandService implements CrudOperationService<Brand> {

	private static final Logger logger = Logger.getLogger(BrandService.class);

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private UserService userService;

	@Override
	public Brand getById(long id) {
		Optional<Brand> brandInDb = brandRepo.findById(id);
		if (brandInDb.isEmpty()) {
			throw new NotFoundException("Brand with id " + id + " not found");
		}
		logger.info("Returned brand with id=" + id);
		return brandInDb.get();
	}

	@Override
	public List<Brand> getAll() {
		logger.info("Returned all brands");
		return brandRepo.findAll();
	}

	@Override
	public Brand add(Brand data, String modifiedBy) {
		data.setCreationDate(new Date());
		data.setModifiedDate(new Date());
		data.setModifiedBy(userService.getUserByUsername(modifiedBy));
		brandRepo.saveAndFlush(data);
		logger.info("Added brand with id=" + data.getId());
		return data;
	}

	@Override
	public Brand update(Brand data, long id, String modifiedBy) {
		Optional<Brand> brandInDb = brandRepo.findById(id);
		if (brandInDb.isEmpty()) {
			throw new NotFoundException("Brand with id " + data + " not found");
		}
		Brand brandData = brandInDb.get();
		if (data.getName() != null)
			brandData.setName(data.getName());
		brandData.setModifiedBy(userService.getUserByUsername(modifiedBy));
		brandData.setModifiedDate(new Date());
		logger.info("Updated brand with id=" + data);
		brandRepo.saveAndFlush(brandData);
		return brandData;
	}

	@Override
	public void delete(long id) {
		boolean brandPresent = brandRepo.existsById(id);
		if (!brandPresent) {
			throw new NotFoundException("Brand with id " + id + " not found");
		}
		logger.info("Deleted brand with id=" + id);
		brandRepo.deleteById(id);
	}

	@Override
	public List<Brand> addAll(List<Brand> data, String modifiedBy) {
		User modifiedByUser = userService.getUserByUsername(modifiedBy);
		data.stream().forEach(brand -> {
			brand.setCreationDate(new Date());
			brand.setModifiedDate(new Date());
			brand.setModifiedBy(modifiedByUser);
		});
		logger.info("Added " + data.size() + " brand(s)");
		return brandRepo.saveAll(data);
	}

}
