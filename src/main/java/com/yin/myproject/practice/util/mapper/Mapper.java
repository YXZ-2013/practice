package com.yin.myproject.practice.util.mapper;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yin.myproject.practice.json.model.Money;
import com.yin.myproject.practice.model.MapperKey;

public class Mapper {

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException,
            InstantiationException {
        T obj = clazz.newInstance();
        obj = map2Object(map, clazz, obj);
        Class<?> supClass = clazz.getSuperclass();
        if (supClass != null) {
            while (supClass != Object.class) {
                obj = map2Object(map, supClass, obj);
                supClass = supClass.getSuperclass();
            }
        }
        return obj;
    }

    protected static <T> T map2Object(Map<String, Object> map, Class<?> clazz, T t) throws IllegalAccessException,
            InstantiationException {

        for (Field field : clazz.getDeclaredFields()) {
            MapperKey annotation = field.getAnnotation(MapperKey.class);
            if (annotation != null) {
                Object value = map.get(annotation.value());
                if (value != null) {
                    field.setAccessible(true);
                    try {
                        field.set(t, convert2Obj(annotation.type(), value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return t;
    }

    public static Object convert2Obj(Class<?> targetClass, Object val) {
        Object obj = val;
        if (val != null && val.getClass().equals(targetClass))
            return obj;
        if (targetClass.equals(java.sql.Date.class)) {
            java.sql.Date sqlDate = (java.sql.Date) val;
            obj = new Date(sqlDate.getTime());
        } else if (targetClass.equals(Integer.class)) {
            obj = Integer.parseInt(String.valueOf(val));
        } else if (val != null && targetClass.equals(Money.class)) {
            obj = new Money(new BigDecimal(String.valueOf(val)));
        } else if (val != null && targetClass.equals(Boolean.class)) {
            obj = new Boolean((String.valueOf(val)));
        }

        //
        return obj;
    }

    public static Map<String, Object> object2Map(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();

        object2Map(map, obj.getClass(), obj);

        Class<?> supClass = obj.getClass().getSuperclass();
        if (supClass != null) {
            while (supClass != Object.class) {
                object2Map(map, supClass, obj);
                supClass = supClass.getSuperclass();
            }
        }

        return map;
    }

    protected static void object2Map(Map<String, Object> map, Class<?> clazz, Object srcObject)
            throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            MapperKey annotation = field.getAnnotation(MapperKey.class);
            if (annotation != null) {
                field.setAccessible(true);
                Object value = field.get(srcObject);
                map.put(annotation.value(), convert2Obj(annotation.type(), value));

            }
        }
    }

    public static void fillObject(Object target, Object source) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        if (target == null || source == null)
            return;

        for (Class<?> cls = target.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    Field sourceField = getField(source.getClass(), field.getName());
                    if (sourceField != null) {
                        field.setAccessible(true);
                        sourceField.setAccessible(true);
                        field.set(target, sourceField.get(source));
                    }
                }
            }
        }
    }

    private static Field getField(Class<?> cls, String name) {

        Field sourceField = null;
        for (; cls != null && cls != Object.class; cls = cls.getSuperclass()) {
            try {
                sourceField = cls.getDeclaredField(name);
                if (sourceField != null)
                    break;
            } catch (NoSuchFieldException e) {
            } catch (SecurityException e) {
            }
        }
        return sourceField;
    }

}
