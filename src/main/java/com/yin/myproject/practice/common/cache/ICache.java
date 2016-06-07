package com.yin.myproject.practice.common.cache;

public interface ICache<E> {
	boolean cached(String key, E entity);

	boolean cached(String key, E entity, int expTime);

	boolean cached(String key, String value, int expTime);

	void drop(E entity);

	void drop(String key);

	E getCacheObject(String key);

	String getCacheString(String key);
}
