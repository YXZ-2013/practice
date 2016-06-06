package com.yin.myproject.practice.common.storage.mysql;

public class UnitOfWorkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnitOfWorkException(String message) {
		super(message);
	}

	public UnitOfWorkException(String message, Throwable cause) {
		super(message, cause);
	}
}
