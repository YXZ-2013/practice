package com.yin.myproject.practice.util.ssdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yin.myproject.practice.util.ssdb.core.Response;
import com.yin.myproject.practice.util.ssdb.core.SSdb;
import com.yin.myproject.practice.util.ssdb.core.SSdbCoderUtil;

public class SSdbUtil {
	private static final Logger logger = LoggerFactory.getLogger(SSdbUtil.class);

	private SSdb ssdb = null;

	public SSdbUtil(String host, int port) {
		try {
			ssdb = new SSdb(host, port);
		} catch (Exception e) {
			logger.error("initail ssdb error : {}" + e.getMessage());
		}
	}

	public SSdbUtil(String host, int port, int timeOutMs) {
		try {
			ssdb = new SSdb(host, port, timeOutMs);
		} catch (Exception e) {
			logger.error("initail ssdb error : {}" + e.getMessage());
		}
	}

	public boolean isConnected() {
		return ssdb.isConnected();
	}

	public boolean isOpen() {
		return isConnected() && !ssdb.isClosed();
	}

	public void close() {
		if (ssdb != null && !ssdb.isClosed()) {
			ssdb.close();
		}
	}

	public boolean set(String key, String value) {
		try {
			ssdb.set(key, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setExp(String key, String value, int seconds) {
		try {
			ssdb.setExp(key, value, seconds);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String get(String key) {
		try {
			return new String(ssdb.get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> boolean setPojo(String key, T value) {
		try {
			ssdb.set(key, SSdbCoderUtil.encode(value));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public <T extends Serializable> boolean setPojoExp(String key, T value, int seconds) {
		try {
			ssdb.setExp(key, SSdbCoderUtil.encode(value), seconds);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public <T extends Serializable> T getPojo(String key) {
		try {
			return SSdbCoderUtil.decode(ssdb.get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> void mSet(List<String> keys, List<T> values) throws Exception {
		try {
			if (keys.size() != values.size()) {
				throw new SSdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for (int i = 0; i < length; i++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = SSdbCoderUtil.encode(values.get(i));
			}
			ssdb.multi_set(kvs);
		} catch (Exception e) {
			throw e;
		}
	}

	public <T extends Serializable> List<T> mGet(List<String> keys) {
		List<T> rs = new ArrayList<T>();
		try {
			Response response = ssdb.multi_get(keys.toArray(new String[] {}));
			for (byte[] value : response.items.values()) {
				T obj = SSdbCoderUtil.decode(value);
				rs.add(obj);
			}
			return rs;
		} catch (Exception e) {
			return null;
		}
	}

	public void setNx(String key, String value) {
		try {
			ssdb.setnx(key, value);
		} catch (Exception e) {
		}
	}

	public <T extends Serializable> void setPojoNx(String key, T value) {
		try {
			ssdb.setnx(key, SSdbCoderUtil.encode(value));
		} catch (Exception e) {
		}
	}

	public long increase(String key, long delt) {
		try {
			return ssdb.incr(key, delt);
		} catch (Exception e) {
			return -12345678900l;
		}
	}

	public void delete(String key) throws Exception {
		ssdb.del(key);
	}
}
