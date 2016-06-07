package com.yin.myproject.practice.common.storage.ssdb;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yin.myproject.practice.common.query.QueryParams;
import com.yin.myproject.practice.common.storage.StorageProvider;
import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.result.PaginatedResult;
import com.yin.myproject.practice.util.result.StorageResult;
import com.yin.myproject.practice.util.ssdb.SSdbException;
import com.yin.myproject.practice.util.ssdb.pool.SSdbClient;
import com.yin.myproject.practice.util.status.StatusCodes;

@Component
public class SSdbProvider<E extends Entity> implements StorageProvider<E> {

	private static final Logger logger = LoggerFactory.getLogger(SSdbProvider.class);

	@Inject
	protected SSdbClient ssdbClient;

	protected String providerMapperName;

	public void setProviderMapperName(String providerMapperName) {
		this.providerMapperName = providerMapperName;
	}

	public StorageResult<E> create(E entity) {
		logger.info("create table {} entry by {}", providerMapperName, entity);
		StorageResult<E> result = update(entity, null, null);
		if (result.isSuccessful()) {
			result.setStatus(StatusCodes.CREATED);
		}
		return result;
	}

	public StorageResult<E> update(E entity, QueryParams queryParams, Map<String, Object> queryMap) {
		logger.info("Update table {} entry by {}", providerMapperName, entity);
		try {
			ssdbClient.hSetPojo(providerMapperName, (String) entity.getId(), entity);
			return new StorageResult<E>(StatusCodes.NO_CONTENT, true);
		} catch (SSdbException e) {
			logger.error("update table error,error message is {}", e.getMessage());
			e.printStackTrace();
		}
		return new StorageResult<E>(StatusCodes.COMMAND_FAILED, false);
	}

	public StorageResult<E> delete(Object... ids) {
		logger.info("Delete table {} entry by {}", providerMapperName, ids);
		for (Object id : ids) {
			ssdbClient.hDelete(providerMapperName, (String) id);
		}
		return new StorageResult<E>(StatusCodes.NO_CONTENT, true);
	}

	public StorageResult<E> get(String id) {
		logger.info("Get table {} by id {}", providerMapperName, id);
		E entity = ssdbClient.hGetPojo(providerMapperName, id);
		StorageResult<E> result = new StorageResult<E>(StatusCodes.NO_CONTENT, false);
		if (entity != null) {
			result.setStatus(StatusCodes.OK);
			result.setSuccessful(true);
			result.setEntity(entity);
		}
		return result;
	}

	public StorageResult<E> getAll(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap) {

		return null;
	}

	public PaginatedResult<E> flipPage(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap,
			int pageNum, int pageSize) {
		return null;
	}

	public long count() {
		return ssdbClient.hSize(providerMapperName);
	}

	public boolean cached(String key, E entity) {
		return ssdbClient.setPojo(key, entity);
	}

	public boolean cached(String key, String value, int expTime) {
		if (Integer.toString(expTime) == null) {
			expTime = 24 * 60 * 60 * 10000;// Ä¬ÈÏ24Ð¡Ê±
		}
		return ssdbClient.setExp(key, value, expTime);
	}

	public E getCacheObject(String key) {
		return ssdbClient.getPojo(key);
	}

	public String getCacheString(String key) {
		return ssdbClient.get(key);
	}

	public void drop(String key) {
		ssdbClient.delete(key);
	}

	public void drop(E entity) {
		ssdbClient.delete((String) entity.getId());
	}
}
