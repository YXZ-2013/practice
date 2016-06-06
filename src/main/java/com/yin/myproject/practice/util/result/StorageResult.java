package com.yin.myproject.practice.util.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.error.ErrorCode;

public class StorageResult<E extends Entity> extends GenericResult<E> {

	private static final long serialVersionUID = 1L;

	private List<E> entities;

	public StorageResult(int status) {
		super(status);
	}

	public StorageResult(int status, boolean isSuccessful) {
		super(status, isSuccessful);
	}

	public StorageResult(int status, boolean isSuccessful, E entity) {
		super(status, isSuccessful);
		setEntity(entity);
	}

	@SafeVarargs
	public StorageResult(int status, boolean isSuccessful, E... entity) {

		this(status, isSuccessful, Arrays.asList(entity));
	}

	public StorageResult(int status, boolean isSuccessful, List<E> entity) {
		super(status, isSuccessful);
		setEntities(entity);
	}

	public StorageResult(int status, boolean isSuccessful, ErrorCode... errorCodes) {
		super(status, isSuccessful, errorCodes);
	}

	public StorageResult(int status, boolean isSuccessful, String... faults) {
		super(status, isSuccessful, faults);
	}

	public StorageResult(boolean isSuccessful, int status, List<String> faults) {
		this(status, isSuccessful, faults.toArray(new String[0]));
	}

	public E getEntity() {
		if (entities == null || entities.size() <= 0) {
			return null;
		}
		return entities != null ? entities.get(0) : null;
	}

	public void setEntity(E entity) {
		if (entities == null) {
			entities = new ArrayList<E>();
		}
		entities.add(entity);
	}

	public List<E> getEntities() {
		return entities;
	}

	public void setEntities(List<E> entities) {
		this.entities = entities;
	}

	@Override
	public String toString() {
		return "StorageResult{" + " entities=" + entities + "} " + super.toString();
	}

}
