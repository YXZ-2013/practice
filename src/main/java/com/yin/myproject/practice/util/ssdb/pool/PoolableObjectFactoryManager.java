package com.yin.myproject.practice.util.ssdb.pool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * 多服务连接信息管理
 * 
 * @author XunzhiYin
 *
 */
public class PoolableObjectFactoryManager {

	/**
	 * 多服务连接信息列表
	 */
	private List<PoolableObjectFactory> factorys = new ArrayList<PoolableObjectFactory>();

	/* 当前连接索引位置 */
	private int currentConnectionIndex = 0;

	public PoolableObjectFactoryManager(String hostPortTimeOut) {
		String[] hosters = hostPortTimeOut.split(";");
		// 剔除重复的服务器信息
        Set<String> filter = new HashSet<String>();
		for (String hoster : hosters) {
			if (StringUtils.isNotBlank(hoster)) {
				String[] vals = hoster.trim().split(":");
				String host = vals[0];
				if (StringUtils.isBlank(host)) {
					continue;
				}
				// 设置端口；默认端口8888
				int port = getIntValue(vals[1], Protocol.DEFAULT_PORT);
				// 设置超时，默认4500毫秒
				int timeout = getIntValue(vals[2], Protocol.DEFAULT_TIMEOUT);
				if (filter.contains(host + "_" + port)) {
					continue;
				}
				try {
					SSdbFactory factory = new SSdbFactory(host, port, timeout);
					// 创建的服务器连接信息加入到列表
					factorys.add(factory);
					filter.add(host + "_" + port);
					System.out.println("create host config :" + host + ":" + port + "|" + timeout);
				} catch (Exception e) {
				}
			} else {
				System.out.println("initial ssdb warring: " + "null host setting.");
			}
		}
	}

	/**
	 * 获取下一个服务器连接信息 采用轮询方式
	 * 
	 * @return
	 */
	public synchronized PoolableObjectFactory getNext() {
		if (currentConnectionIndex > factorys.size() - 1) {
			currentConnectionIndex = 0;
		}
		PoolableObjectFactory temp = factorys.get(currentConnectionIndex);
		currentConnectionIndex++;
		return temp;
	}

	private int getIntValue(String v, int dft) {
		try {
			if (StringUtils.isNumeric(v.trim())) {
				return Integer.parseInt(v);
			}
		} catch (Exception e) {

		}
		return dft;
	}
}
