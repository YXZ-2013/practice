package com.yin.myproject.practice.pattern.repository;

import com.yin.myproject.practice.util.result.StorageResult;

public interface GenericRepository<E> {
	
	public StorageResult<?> load(String id);
	
	public StorageResult<?> delete(String id);
	
	public StorageResult<?> delete(E entity);
	
	public StorageResult<?> create(E entity);
	
	public StorageResult<?> update(E entity);
	
	public String getRepositoryName();
}
