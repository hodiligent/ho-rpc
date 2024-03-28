package com.ho.rpc.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/28 09:19
 */
public class MockUtil {
    public static Object mock(Class type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return 2.1;
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return 2.2f;
        } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return false;
        } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return 'a';
        } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return 3;
        } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return 4;
        } else if (type.isEnum()) {
            return type.getEnumConstants()[0];
        } else if (type.isArray()) {
            return mock(type.getComponentType());
        } else if (type.isInterface()) {
            return mock(Object.class);
        } else if (type.isPrimitive()) {
            return null;
        }
        if (Number.class.isAssignableFrom(type)) {
            return 5;
        }
        if (type.equals(String.class)) {
            return "this_is_a_mock_string";
        }

        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class<?> type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Class<?> fType = f.getType();
            Object fValue = mock(fType);
            f.set(result, fValue);
        }
        return result;
    }
}
