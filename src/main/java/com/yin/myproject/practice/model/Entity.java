package com.yin.myproject.practice.model;

import java.io.Serializable;

public class Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8650323332043129283L;
	
	@MapperKey(value="id")
	protected Object id;

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}
	
	public static long currentTimeMillis(){
		return System.currentTimeMillis();
	}
}
