package com.yin.myproject.practice.pattern.repository;

import com.mysql.jdbc.StringUtils;
import com.yin.myproject.practice.common.cache.ICache;
import com.yin.myproject.practice.model.Entity;

public abstract class CanCacheRepository<E extends Entity> extends GenericRepositoryImpl<E> implements ICache<E> {
	public boolean cached(String key, E entity) {
		if(entity == null){
			return false;
		}
		if(StringUtils.isNullOrEmpty(key)){
			return false;
		}
		return getSSDBPro
	}
}
