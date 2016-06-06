package com.yin.myproject.practice.common.storage;

import java.util.List;
import java.util.Map;

import com.yin.myproject.practice.common.query.QueryParams;
import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.result.PaginatedResult;
import com.yin.myproject.practice.util.result.StorageResult;

public interface StorageProvider<E extends Entity> {

	public StorageResult<E> create(final E entity);

	StorageResult<E> update(E entity, QueryParams queryParams, Map<String, Object> queryMap);

	StorageResult<E> delete(Object... ids);

	StorageResult<E> get(final String id);

	StorageResult<E> getAll(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap);

	/**
	 * ∑≠“≥≤È—Ø
	 * 
	 * @param viewer
	 * @param queryParams
	 * @param queryMap
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PaginatedResult<E> flipPage(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap, int pageNum,
			int pageSize);
}
