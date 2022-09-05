package com.saravanank.ecommerce.resourceserver.service;

import java.util.List;

public interface CrudOperationService<T> {

	public T getById(long id);

	public List<T> getAll(String search);

	public List<T> addAll(List<T> data, String modifiedBy);

	public T add(T data, String modifiedBy);

	public T update(T data, long id, String modifiedBy);

	public void delete(long id);
	
	public List<OptionValue> getAllForOption();

}
