package com.ho.rpc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 09:27
 */
public class MethodUtil {

    /**
     * 识别非本地方法
     *
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * 获取方法签名
     *
     * @param method
     * @return
     */
    public static String getMethodSign(Method method) {
        // 获取方法名称
        String methodName = method.getName();

        // 获取方法参数列表
        Parameter[] parameters = method.getParameters();

        // 构建方法签名字符串
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(methodName).append("#").append("(");

        // 遍历参数列表并添加参数类型
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            signBuilder.append(parameter.getType().getSimpleName());

            if (i < parameters.length - 1) {
                signBuilder.append(", ");
            }
        }

        signBuilder.append(")");

        return signBuilder.toString();
    }


    /**
     * 找到使用了HoConsumer注解
     *
     * @param clazz
     * @return
     */
    public static List<Field> findAnnotatedField(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
