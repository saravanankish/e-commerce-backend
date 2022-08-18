package com.saravanank.ecommerce.resourceserver.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saravanank.ecommerce.resourceserver.service.SubCategoryService;

@RestController
@RequestMapping("/v1/sub-category")
public class SubCategoryController {

	private static final Logger logger = Logger.getLogger(SubCategoryController.class);

	@Autowired
	private SubCategoryService subCategoryService;

	@GetMapping
	public ResponseEntity<List<String>> getAllSubCategories() {
		logger.info("GET request to /api/v1/sub-category");
		return new ResponseEntity<List<String>>(subCategoryService.getAll(), HttpStatus.OK);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<String>> getAllSubCategoryInCategory(@PathVariable("categoryId") long categoryId) {
		logger.info("GET request to /api/v1/sub-category/category/" + categoryId);
		return new ResponseEntity<List<String>>(subCategoryService.getAllInId(categoryId), HttpStatus.OK);
	}

	@PostMapping("/all/{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<List<String>> addAllToCategory(@PathVariable("categoryId") long categoryId,
			Principal principal, @RequestBody Set<String> data) {
		return new ResponseEntity<List<String>>(subCategoryService.addAllToId(data, principal.getName(), categoryId),
				HttpStatus.CREATED);
	}

	@PostMapping("/{categoryId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> addToCategory(@PathVariable("categoryId") long categoryId, Principal principal,
			@RequestBody String data) {
		return new ResponseEntity<String>(subCategoryService.add(data, principal.getName(), categoryId),
				HttpStatus.CREATED);
	}

	@PutMapping("/{categoryId}/{index}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> updateSubCategory(@PathVariable("categoryId") long categoryId,
			@PathVariable("index") int index, Principal principal, @RequestBody String data) {
		return new ResponseEntity<String>(subCategoryService.update(data, index, principal.getName(), categoryId),
				HttpStatus.CREATED);
	}

	@DeleteMapping("/{categoryId}/{index}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteSubCategory(@PathVariable("categoryId") long categoryId,
			@PathVariable("index") int index, Principal principal) {
		subCategoryService.delete(index, categoryId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
