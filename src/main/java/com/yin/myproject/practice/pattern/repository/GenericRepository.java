package com.yin.myproject.practice.pattern.repository;

import com.yin.myproject.practice.util.result.StorageResult;

public interface GenericRepository<E> {
	
	public StorageResult<?> load(String id);
}
