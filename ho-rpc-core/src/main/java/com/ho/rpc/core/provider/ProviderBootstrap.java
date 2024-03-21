package com.ho.rpc.core.provider;

import com.ho.rpc.core.annotation.HoProvider;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:19
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String, Object> interfaceCache = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(HoProvider.class);
        for (Map.Entry<String, Object> providerEntry : providers.entrySet()) {
            Class<?> inter = providerEntry.getValue().getClass().getInterfaces()[0];
            this.interfaceCache.put(inter.getCanonicalName(), providerEntry.getValue());
        }
    }

    /**
     * 触发调用
     *
     * @param request
     * @return
     */
    public RpcResponse invoke(RpcRequest request) {
        Object bean = interfaceCache.get(request.getService());
        if (Objects.isNull(bean)) {
            throw new IllegalArgumentException(String.format("Don't get %s service", request.getService()));
        }
        try {
            Method method = findMethod(bean.getClass(), request.getMethod());
            if (Objects.isNull(method)) {
                throw new IllegalArgumentException(String.format("Don't find %s method.", request.getMethod()));
            }
            Object result = method.invoke(bean, request.getArgs());
            return RpcResponse.ofSuccess(result);
        } catch (Exception e) {
            return RpcResponse.ofFail(e.getMessage());
        }
    }

    /**
     * 遍历寻找对应方法名
     *
     * @param clazz
     * @param methodName
     * @return
     */
    private Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }
}
