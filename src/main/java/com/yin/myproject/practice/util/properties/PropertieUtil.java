package com.yin.myproject.practice.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertieUtil {

	/**
	 * 存储已加载的配置文件信息
	 */
	private static Map<String, Properties> _properties = new HashMap<String, Properties>();

	/**
	 * 加载指定的配置文件名称，文件后缀为properties
	 * 
	 * @param propsName
	 * @return
	 */
	public static Properties getProps(String propsName) {
		if (_properties.get(propsName) != null) {
			return _properties.get(propsName);
		}
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			@SuppressWarnings("unused")
			String packagePath = PropertieUtil.class.getResource("/").getPath();
			File file = new File(URLDecoder.decode(
					Properties.class.getClassLoader().getResource(propsName + ".properties").getFile(), "utf-8"));
			fis = new FileInputStream(file);
			props.load(fis);
		} catch (Exception e) {

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			_properties.put(propsName, props);
		}
		return props;
	}

	/**
	 * 获取指定配置文件中，指定字段的值
	 * 
	 * @param propsName
	 *            文件名
	 * @param propName
	 *            字段
	 * @return
	 */
	public static String getStrProp(String propsName, String propName) {
		Properties props = getProps(propsName);
		if (props != null) {
			String propValue = props.getProperty(propName);
			if (propValue != null && !propValue.isEmpty()) {
				return propValue;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param propsName
	 * @param propName
	 * @return
	 */
	public static int getIntProp(String propsName, String propName) {
		Properties props = getProps(propsName);
		if (props != null) {
			String propValue = props.getProperty(propName);
			if (propValue != null && !propValue.isEmpty()) {
				return Integer.parseInt(propValue);
			}
		}
		return 0;
	}

	public static float getFloatProp(String propsName, String propName) {
		Properties props = getProps(propsName);
		if (props != null) {
			String propValue = props.getProperty(propName);
			if (propValue != null && !propValue.isEmpty()) {
				return Float.parseFloat(propValue);
			}
		}
		return 0.0f;
	}

	public static long getLongProp(String propsName, String propName) {
		Properties props = getProps(propsName);
		if (props != null) {
			String propValue = props.getProperty(propName);
			if (propValue != null && !propValue.isEmpty()) {
				return Long.parseLong(propValue);
			}
		}
		return 0l;
	}
}
