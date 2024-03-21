package com.ho.rpc.core.consumer;

import com.ho.rpc.core.annotation.HoConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/21 09:04
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String, Object> interfaceCache = new HashMap<>();


    public void start() {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());

            fields.forEach(field -> {
                Class<?> service = field.getType();
                String serviceName = service.getCanonicalName();
                Object consumer = interfaceCache.get(serviceName);
                if (Objects.isNull(consumer)) {
                    consumer = createConsumer(service);
                    interfaceCache.put(serviceName, consumer);
                }
                try {
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 创建消费者
     *
     * @param service
     * @return
     */
    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new HoInvocationHandler(service));
    }

    /**
     * 找到使用了HoConsumer注解
     *
     * @param clazz
     * @return
     */
    private List<Field> findAnnotatedField(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(HoConsumer.class)) {
                    result.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
