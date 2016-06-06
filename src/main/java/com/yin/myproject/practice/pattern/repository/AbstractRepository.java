package com.yin.myproject.practice.pattern.repository;

import com.yin.myproject.practice.common.storage.StorageProvider;
import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.result.StorageResult;

public abstract class AbstractRepository<E extends Entity> implements GenericRepository<E> {
	public abstract StorageProvider<E> getStorageProvider();

	public StorageResult<?> create(E entity) {
		return this.getStorageProvider().create(entity);
	}

	public StorageResult<?> delete(E entity) {
		return this.getStorageProvider().delete(entity.getId());
	}

	public StorageResult<?> delete(String id) {
		return this.getStorageProvider().delete(id);
	}

	public StorageResult<?> load(String id) {
		return this.getStorageProvider().get(id);
	}

	public StorageResult<?> update(E entity) {
		return this.getStorageProvider().update(entity, null, null);
	}
}
