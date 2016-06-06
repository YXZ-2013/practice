package com.yin.myproject.practice.util.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.yin.myproject.practice.util.error.ErrorCode;


public class GenericResult<T> implements KyMessage {

	private static final long serialVersionUID = -5589109061538662986L;

	protected int status;
	protected boolean isSuccessful;
	protected List<ErrorCode> errorCodes = new LinkedList<ErrorCode>();
	protected List<String> faultedProperties = new LinkedList<String>();

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public List<ErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<ErrorCode> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public List<String> getFaultedProperties() {
		return faultedProperties;
	}

	public void setFaultedProperties(List<String> faultedProperties) {
		this.faultedProperties = faultedProperties;
	}

	public GenericResult() {
	}
	
	public GenericResult(int status) {
        this.status = status;
    }

    public GenericResult(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public GenericResult(int status, boolean isSuccessful) {
        this.status = status;
        this.isSuccessful = isSuccessful;
    }

    public GenericResult(int status, boolean isSuccessful, ErrorCode... errorCodes) {
        this.status = status;
        this.isSuccessful = isSuccessful;
        this.errorCodes.addAll(Arrays.asList(errorCodes));
    }

    public GenericResult(int status, boolean isSuccessful, String... faultedProperty) {
        this.status = status;
        this.isSuccessful = isSuccessful;
        this.faultedProperties.addAll(Arrays.asList(faultedProperty));
    }
}
