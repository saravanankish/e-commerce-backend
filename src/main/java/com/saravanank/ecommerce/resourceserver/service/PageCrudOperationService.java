package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;

public interface PageCrudOperationService<T, R> extends CrudOperationService<T> {

	@Override
	default List<T> getAll(String search) {
		// TODO Auto-generated method stub
		return null;
	}

	public R getAll(Integer page, Integer limit, String search);
	
}
