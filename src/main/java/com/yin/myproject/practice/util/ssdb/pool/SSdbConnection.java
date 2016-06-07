package com.yin.myproject.practice.util.ssdb.pool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yin.myproject.practice.util.ssdb.SSdbException;
import com.yin.myproject.practice.util.ssdb.core.Response;
import com.yin.myproject.practice.util.ssdb.core.SSdb;
import com.yin.myproject.practice.util.ssdb.core.SSdbCoderUtil;

public class SSdbConnection {
	private SSdb ssdb = null;

	public SSdbConnection(String host, int port) {
		try {
			ssdb = new SSdb(host, port);
		} catch (Exception e) {
			System.out.println("initial ssdb error : " + e.getMessage());
		}
	}

	public SSdbConnection(String host, int port, int timeOutMs) {
		try {
			ssdb = new SSdb(host, port, timeOutMs);
		} catch (Exception e) {
			System.out.println("initial ssdb error : " + e.getMessage());
		}
	}

	public boolean testConnected() {
		try {
			ssdb.get("test");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isOpen() {
		return isConnected() && !ssdb.isClosed();
	}

	public boolean isConnected() {
		try {
			return ssdb.isConnected();
		} catch (Exception e) {
			return false;
		}
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

	public <T extends Serializable> void mSet(List<String> keys, List<String> values) throws Exception {
		try {
			if (keys.size() != values.size()) {
				throw new SSdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for (int i = 0; i < length; i++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = values.get(i).getBytes();
			}
			ssdb.multi_set(kvs);
		} catch (Exception e) {
			throw e;
		}
	}

	public <T extends Serializable> void mSetPojo(List<String> keys, List<T> values) throws Exception {
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

	public <T extends Serializable> List<T> mGetPojo(List<String> keys) {
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

	public Map<String, String> mGet(List<String> keys) {
		return mGet(keys.toArray(new String[] {}));
	}

	public Map<String, String> mGet(String[] keys) {
		Map<String, String> rs = new HashMap<String, String>();
		try {
			Response response = ssdb.multi_get(keys);
			for (Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.put(new String(en.getKey()), new String(en.getValue()));
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<KeyValueBean> mGetAsList(String[] keys) {
		List<KeyValueBean> rs = new ArrayList<KeyValueBean>();
		try {
			Response response = ssdb.multi_get(keys);
			for (Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.add(new KeyValueBean(new String(en.getKey()), new String(en.getValue())));
			}
			return rs;
		} catch (Exception e) {
			return rs;
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

	public void multiHSet(String mapName, List<String> keys, List<String> values) {
		try {
			if (keys.size() != values.size()) {
				throw new SSdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for (int i = 0; i < length; i++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = values.get(i).getBytes();
			}
			ssdb.multi_hset(mapName, kvs);
		} catch (Exception e) {

		}
	}

	public Map<String, String> hGetAll(String mapName) {
		Map<String, String> rs = new HashMap<String, String>();
		try {
			Response response = ssdb.hgetall(mapName);
			for (Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.put(new String(en.getKey()), new String(en.getValue()));
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void hSet(String mapperName, String key, String value) {
		try {
			ssdb.hset(mapperName, key, value);
		} catch (Exception e) {

		}
	}

	public String hGet(String mapperName, String key) {
		try {
			byte[] temp = ssdb.hget(mapperName, key);
			if (temp == null) {
				return null;
			}
			return new String(temp);
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> void hSetPojo(String mapperName, String key, T value) {
		try {
			ssdb.hset(mapperName, key, SSdbCoderUtil.encode(value));
		} catch (Exception e) {

		}
	}

	public <T extends Serializable> T hGetPojo(String mapperName, String key) {
		try {
			byte[] temp = ssdb.hget(mapperName, key);
			if (temp == null) {
				return null;
			}
			return SSdbCoderUtil.decode(temp);
		} catch (Exception e) {
			return null;
		}
	}

	public void hDelete(String mapperName, String keyName) {
		try {
			ssdb.hdel(mapperName, keyName);
		} catch (Exception e) {

		}
	}

	public void hClear(String mapperName) throws Exception {
		ssdb.hclear(mapperName);
	}

	public long hSize(String mapperName) throws Exception {
		return ssdb.hsize(mapperName);
	}

	public void qPush(String queueName, String value) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			ssdb.qPush(queueName.getBytes(), value.getBytes());
		} catch (Exception e) {
			throw new SSdbException("qPush error : " + e.getMessage());
		}
	}

	public String qPop(String queueName) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			Response response = ssdb.qPop(queueName.getBytes());
			return new String(response.raw.get(1));
		} catch (Exception e) {
			return null;
		}
	}

	public String qPopBack(String queueName) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			Response response = ssdb.qPopBack(queueName.getBytes());
			return new String(response.raw.get(1));
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> void qPushPojo(String queueName, T value) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			ssdb.qPush(queueName.getBytes(), SSdbCoderUtil.encode(value));
		} catch (Exception e) {
			throw new SSdbException("qPushPojo error : " + e.getMessage());
		}
	}

	public <T extends Serializable> T qPopPojo(String queueName) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			Response response = ssdb.qPop(queueName.getBytes());
			return SSdbCoderUtil.decode(response.raw.get(1));
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> T qPopBackPojo(String queueName) throws SSdbException {
		if (StringUtils.isBlank(queueName)) {
			throw new SSdbException("argument[queueName] can not be null");
		}
		try {
			Response response = ssdb.qPopBack(queueName.getBytes());
			return SSdbCoderUtil.decode(response.raw.get(1));
		} catch (Exception e) {
			return null;
		}
	}

	public long qSize(String queueName) {
		try {
			Response response = ssdb.qSize(queueName.getBytes());
			return Long.parseLong(new String(response.raw.get(1)));
		} catch (Exception e) {
			return 0;
		}
	}

	public void zSet(String name, String key, long score) {
		try {
			ssdb.zset(name, key, score);
		} catch (Exception e) {

		}

	}

	public long zGet(String name, String key) {
		try {
			return ssdb.zget(name, key);
		} catch (Exception e) {
		}
		return -1;
	}

	public void zDelete(String name, String key) {
		try {
			ssdb.zdel(name, key);
		} catch (Exception e) {
		}
	}

	public List<String> zScan(String name, String key, Long score_start, Long score_end, int limit) {
		List<String> rs = new ArrayList<String>();
		try {
			Response response = ssdb.zscan(name, key, score_start, score_end, limit);
			for (byte[] rKey : response.keys) {
				rs.add(new String(rKey));
			}
		} catch (Exception e) {

		}
		return rs;
	}

	public long zSize(String name) {
		long count = 0;
		try {
			count = ssdb.zsize(name);
		} catch (Exception e) {

		}
		return count;
	}

	public String hostInfo() {
		try {
			return ssdb.info();
		} catch (Exception e) {
			return "";
		}
	}
}
