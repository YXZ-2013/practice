package com.yin.myproject.practice.util.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.error.ErrorCode;

@SuppressWarnings("rawtypes")
public class Result extends StorageResult {

	private static final long serialVersionUID = -1913377753540637378L;
	private static final Logger logger = LoggerFactory.getLogger(StorageResult.class);

	public enum ResultPart {
		TOANL_COUNT, MESSAGE, TYPE
	}

	private Map<ResultPart, Object> resultParts = new HashMap<ResultPart, Object>();

	public Result(int status, boolean successful) {
		super(status, successful);
	}
	
	@SuppressWarnings("unchecked")
	public Result(int status, boolean successful, Entity... entity) {
		super(status, successful, entity);
	}

	@SuppressWarnings("unchecked")
	public Result(int status, boolean successful, List<Entity> entities) {
		super(status, successful, entities);
	}

	public Result(int status, boolean successful, ErrorCode... errorCodes) {
		super(status, successful, errorCodes);
	}

	@SuppressWarnings("unchecked")
	public Result(int status, boolean successful, Entity entity, ErrorCode errorCodes) {
		super(status, successful, errorCodes);
		super.setEntity(entity);
	}

	public Result(int status, boolean successful, String... faults) {
		super(status, successful, faults);
	}

	public Result(boolean successful, int status, List<String> faults) {
		super(status, successful, (String[]) faults.toArray(new String[0]));
	}

	@SuppressWarnings("unchecked")
	public Result(int status, boolean successful, Entity entity, List<String> faults) {
		super(status, successful, (String[]) faults.toArray(new String[0]));
		super.setEntity(entity);
	}

	public Integer getIntegerResult(ResultPart part) {
		Integer resultPart = (Integer) resultParts.get(part);
		if (resultPart == null) {
			logger.debug("Could not find value for part:{}", part);
			throw new RuntimeException("Result part was not found for: " + part);
		}
		return resultPart;
	}

	public Boolean getBooleanResult(ResultPart part) {
		Boolean resultPart = (Boolean) resultParts.get(part);
		if (resultPart == null) {
			logger.debug("Could not find value for part:{}", part);
			throw new RuntimeException("Result part was not found for: " + part);
		}
		return resultPart;
	}

	public Long getLongResult(ResultPart part) {
		Long resultPart = (Long) resultParts.get(part);
		if (resultPart == null) {
			logger.debug("Could not find value for part:{}", part);
			throw new RuntimeException("Result part was not found for: " + part);
		}
		return resultPart;
	}

	public String getStringResult(ResultPart part) {
		String resultPart = (String) resultParts.get(part);
		if (resultPart == null) {
			logger.debug("Could not find value for part:{}", part);
			throw new RuntimeException("Result part was not found for: " + part);
		}
		return resultPart;
	}

	public void setResultPart(ResultPart part, Object value) {

		if (part == null) {
			throw new NullPointerException("part is null");
		}

		if (value == null) {
			throw new NullPointerException("value is null");
		}

		if ((value instanceof Integer) || (value instanceof Boolean) || (value instanceof Long)
				|| (value instanceof String)) {
			resultParts.put(part, value);
		} else {
			throw new RuntimeException("Cannot accept value of type: " + value.getClass());
		}
	}

	@Override
    public String toString() {
        return "Result{" + "resultParts=" + resultParts + "} " + super.toString();
    }
}
