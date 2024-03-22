package com.ho.rpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 10:13
 */
public class TypeUtil {

    private final static String ORIGINAL_PACKAGE_NAME_PREFIX = "java";

    /**
     * 类型转换
     *
     * @param origin
     * @param type
     * @return
     */
    public static Object cast(Object origin, Class<?> type) {
        if (Objects.isNull(origin)) {
            return null;
        }
        Class<?> clazz = origin.getClass();
        if (type.isAssignableFrom(clazz)) {
            return origin;
        }

        if (type.isArray()) {
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith(ORIGINAL_PACKAGE_NAME_PREFIX)) {
                    Array.set(resultArray, i, Array.get(origin, i));
                } else {
                    Array.set(resultArray, i, cast(Array.get(origin, i), componentType));
                }
            }
            return resultArray;
        }

        if (origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if (origin instanceof JSONObject jsonObject) {
            return jsonObject.toJavaObject(type);
        }

        if (type.isPrimitive() || type.getPackageName().startsWith(ORIGINAL_PACKAGE_NAME_PREFIX)) {
            return castToPrimitive(origin, type);
        }

        return null;
    }

    /**
     * 转换为基本/包装类型
     *
     * @param origin
     * @param type
     * @return
     */
    private static Object castToPrimitive(Object origin, Class<?> type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.parseInt(origin.toString());
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.parseLong(origin.toString());
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.parseDouble(origin.toString());
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.parseFloat(origin.toString());
        } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return Boolean.parseBoolean(origin.toString());
        } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.parseByte(origin.toString());
        } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.parseShort(origin.toString());
        } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return origin.toString().charAt(0);
        }
        return null;
    }

    /**
     * 响应结果类型转换
     *
     * @param method
     * @param data
     * @return
     */
    public static Object castMethodResult(Method method, Object data) {
        Class<?> type = method.getReturnType();
        if (data instanceof JSONObject jsonResult) {
            if (Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap();
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length == 2) {
                        Class<?> keyType = (Class<?>) actualTypeArguments[0];
                        Class<?> valueType = (Class<?>) actualTypeArguments[1];
                        resultMap.putAll(jsonResult.entrySet().stream()
                                .collect(Collectors.toMap(
                                        entry -> cast(entry.getKey(), keyType),
                                        entry -> cast(entry.getValue(), valueType),
                                        (oldValue, newValue) -> newValue
                                )));
                    }
                }

                return resultMap;
            }

            return jsonResult.toJavaObject(type);
        }

        if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith(ORIGINAL_PACKAGE_NAME_PREFIX)) {
                        Array.set(resultArray, i, jsonArray.get(i));
                    } else {
                        Array.set(resultArray, i, cast(jsonArray.get(i), componentType));
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(type)) {
                List<Object> resultList = new ArrayList<>(array.length);
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length == 1) {
                        Class<?> actualType = (Class<?>) actualTypeArguments[0];
                        for (Object item : array) {
                            resultList.add(cast(item, actualType));
                        }
                    } else {
                        resultList.addAll(Arrays.asList(array));
                    }
                    return resultList;
                }
            } else {
                return null;
            }
        }

        return cast(data, type);
    }
}
