package com.yin.myproject.practice.util.ssdb;

public class SSdbException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String msg;

	public SSdbException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
