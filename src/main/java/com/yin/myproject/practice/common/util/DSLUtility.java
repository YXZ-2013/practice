package com.yin.myproject.practice.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yin.myproject.practice.json.model.Money;
import com.yin.myproject.practice.util.mapper.Mapper;

public class DSLUtility {
	private final static Logger logger = LoggerFactory.getLogger(DSLUtility.class);

	public static Map<Field<Object>, Object> autoMatchDSL2(Object o) {
		if (o != null) {
			Map<Field<Object>, Object> map = new HashMap<Field<Object>, Object>();
			Method methods[] = o.getClass().getDeclaredMethods();
			Map<String, Object> valueMap = new HashMap<String, Object>();

			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					try {
						if (method.invoke(o) != null) {
							if (method.getReturnType().equals(int.class) || method.getReturnType().equals(Integer.class)
									|| method.getReturnType().equals(long.class)
									|| method.getReturnType().equals(Long.class)
									|| method.getReturnType().equals(char.class)
									|| method.getReturnType().equals(Character.class)
									|| method.getReturnType().equals(byte.class)
									|| method.getReturnType().equals(Byte.class)
									|| method.getReturnType().equals(float.class)
									|| method.getReturnType().equals(Float.class)
									|| method.getReturnType().equals(double.class)
									|| method.getReturnType().equals(Double.class)
									|| method.getReturnType().equals(UUID.class)
									|| method.getReturnType().equals(String.class)
									|| method.getReturnType().equals(Date.class)) {
								String field = method.getName().substring(3);
								valueMap.put(field, method.invoke(o));
							} else {
								String field = method.getName().substring(3);
								Object innerObj = method.invoke(o);

								Method[] methodInners = innerObj.getClass().getMethods();
								for (Method mi : methodInners) {
									if (mi.getName().equals("getId")) {
										valueMap.put(field, mi.invoke(innerObj));
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 把所有值得get方法的值put进map中
					for (Object key : valueMap.keySet()) {
						String sqlField = key.toString();
						map.put(DSL.field(sqlField), valueMap.get(key));
					}
				}
			}
			return map;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<Field<Object>, Object> autoMatchDSL(Object o) {
		Map<String, Object> result = null;
		try {
			if (o instanceof Map) {
				result = (Map<String, Object>) o;
			} else {
				result = Mapper.object2Map(o);
			}
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			return null;
		}
		Map<Field<Object>, Object> fieldMap = new HashMap<Field<Object>, Object>();
		for (Object key : result.keySet()) {
			String sqlField = key.toString();
			fieldMap.put(DSL.field(sqlField), convert2DBValue(result.get(key)));
		}
		return fieldMap;
	}

	public static Map<Field<Object>, Object> autoMatchDSL_2(Map<Field<Object>, Object> map, Object o) {
		if (map != null && o != null) {
			Method methods[] = o.getClass().getDeclaredMethods();
			Map<String, Object> valueMap = new HashMap<String, Object>();

			// 得到有值的get方法
			for (Method m : methods) {
				if (m.getName().startsWith("get")) {
					try {
						if (m.invoke(o) != null) {
							String field = m.getName().substring(3);
							valueMap.put(field, m.invoke(o));
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			// 把有值的get方法的值pu进map中
			for (Object key : valueMap.keySet()) {
				String sqlField = key.toString();
				map.put(DSL.field(sqlField), valueMap.get(key));
			}
			return map;
		}
		return null;
	}

	public static List<Field<Object>> autoAddToList(String... params) {
		List<Field<Object>> viewer = new ArrayList<Field<Object>>();
		if (params != null && params.length > 0) {
			for (String param : params) {
				viewer.add(DSL.field(param));
			}
		}
		return viewer;
	}

	public static List<Map<String, Object>> convertList(Result<Record> results) {
		if (results == null) {
			return null;
		}
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
		for (Record aRecord : results) {
			Map<String, Object> o = convertMap(aRecord);
			if (o != null) {
				ls.add(o);
			}
		}
		return ls;
	}

	public static Map<String, Object> convertMap(Record record) {
		if (record == null) {
			return null;
		}
		int n = record.fields().length;
		Map<String, Object> reMap = new LinkedHashMap<String, Object>();
		for (int i = 0; i < n; i++) {
			String kn = record.field(i).getName().toString();
			Object kv = record.getValue(record.field(i));
			reMap.put(kn, matchJavaValue(kv));
		}
		return reMap;
	}

	protected static Object matchJavaValue(Object value) {
		if (value == null) {
			return value;
		}
		Object returnObject = value;
		if (value.getClass().equals(ULong.class)) {
			returnObject = ((ULong) value).longValue();
		}
		return returnObject;
	}

	public static Object convert2DBValue(Object val) {
		if (val == null)
			return val;
		Object returnObj = val;
		if (val.getClass().equals(Money.class)) {
			returnObj = ((Money) val).doubleValue();
		} else if (val.getClass().equals(java.util.Date.class)) {
			returnObj = new java.sql.Timestamp(((Date) val).getTime());
		}
		return returnObj;

	}
}
