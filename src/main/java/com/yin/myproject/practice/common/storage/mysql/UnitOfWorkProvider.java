package com.yin.myproject.practice.common.storage.mysql;

import java.util.Map;

import com.yin.myproject.practice.common.query.QueryParams;
import com.yin.myproject.practice.model.Entity;

public interface UnitOfWorkProvider<E extends Entity> {
	public void persistCreate(E entity) throws UnitOfWorkException;

	public void persistDelete(E entity) throws UnitOfWorkException;

	public void persistUpdate(E entity, QueryParams queryParams, Map<String, Object> queryMap)
			throws UnitOfWorkException;
}
