package com.saravanank.ecommerce.resourceserver.service;

import java.util.Set;
import java.util.List;

public interface SubCategoryService extends CrudOperationService<String> {

	@Override
	default String getById(long id) {
		return null;
	}

	public List<String> getAllInId(long id);
	
	public List<String> addAllToId(Set<String> data, String modifiedBy, long id);
	
	@Override
	default List<String> addAll(List<String> data, String modifiedBy) {
		return null;
	}

	@Override
	default String add(String data, String modifiedBy) {
		return null;
	}

	public String add(String data, String modifiedBy, long id);
	
	@Override
	default String update(String data, long id, String modifiedBy) {
		// TODO Auto-generated method stub
		return null;
	}

	public String update(String data, int index, String modifiedBy, long id);
	
	@Override
	default void delete(long id) {
	}

	public void delete(int index, long id);
	
}
