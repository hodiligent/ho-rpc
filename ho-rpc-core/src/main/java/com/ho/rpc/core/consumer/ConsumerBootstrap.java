package com.ho.rpc.core.consumer;

import com.ho.rpc.core.annotation.HoConsumer;
import com.ho.rpc.core.api.LoadBalancer;
import com.ho.rpc.core.api.RegistryCenter;
import com.ho.rpc.core.api.Router;
import com.ho.rpc.core.api.RpcContext;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Description 具体消费者启动类
 * @Author LinJinhao
 * @Date 2024/3/21 09:04
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    private ApplicationContext applicationContext;

    private Environment environment;

    private Map<String, Object> interfaceCache = new HashMap<>();

    /**
     * 初始化上下文
     *
     * @return
     */
    private RpcContext initContext() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadBalancer(loadBalancer);

        return rpcContext;
    }

    /**
     * 启动过程
     */
    public void start() {
        // 初始化上下文
        RpcContext rpcContext = initContext();
        // 初始化注册中心
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.forEach(field -> {
                Class<?> service = field.getType();
                String serviceName = service.getCanonicalName();
                Object consumer = interfaceCache.get(serviceName);
                if (Objects.isNull(consumer)) {
                    consumer = createConsumerFromRegistry(service, registryCenter, rpcContext);
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
     * 获取消费者
     *
     * @param service
     * @param registryCenter
     * @param rpcContext
     * @return
     */
    private Object createConsumerFromRegistry(Class<?> service, RegistryCenter registryCenter, RpcContext rpcContext) {
        String serviceName = service.getCanonicalName();
        List<String> providers = registryCenter.fetchAll(serviceName);

        return createConsumer(service, providers, rpcContext);
    }

    /**
     * 创建消费者
     *
     * @param service
     * @return
     */
    private Object createConsumer(Class<?> service, List<String> providers, RpcContext rpcContext) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new HoInvocationHandler(service, providers, rpcContext));
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
