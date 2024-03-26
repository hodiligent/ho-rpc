package com.ho.rpc.core.provider;

import com.ho.rpc.core.annotation.HoProvider;
import com.ho.rpc.core.api.RegistryCenter;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.meta.ProviderMeta;
import com.ho.rpc.core.util.MethodUtil;
import com.ho.rpc.core.util.TypeUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description 具体服务提供者启动类
 * @Author LinJinhao
 * @Date 2024/3/7 00:19
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private RegistryCenter registryCenter;

    private MultiValueMap<String, ProviderMeta> providerCache = new LinkedMultiValueMap<>();

    private String instance;

    @Value("${server.port}")
    private String port;

    @SneakyThrows
    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(HoProvider.class);
        for (Map.Entry<String, Object> providerEntry : providers.entrySet()) {
            Class<?> inter = providerEntry.getValue().getClass().getInterfaces()[0];
            Method[] methods = inter.getMethods();
            for (Method method : methods) {
                if (MethodUtil.checkLocalMethod(method)) {
                    continue;
                }

                ProviderMeta meta = new ProviderMeta();
                meta.setMethod(method);
                meta.setMethodSign(MethodUtil.getMethodSign(method));
                meta.setServiceImpl(providerEntry.getValue());
                providerCache.add(inter.getCanonicalName(), meta);
            }
        }
    }

    /**
     * 启动过程
     */
    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + ":" + port;
        // 启动注册中心
        registryCenter.start();
        // 所有服务进行注册
        providerCache.keySet().forEach(this::registerService);
    }

    /**
     * 服务注册
     *
     * @param service
     */
    private void registerService(String service) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.registry(service, instance);
    }

    /**
     * 销毁过程
     */
    @PreDestroy
    public void stop() {
        // 所有服务进行取消注册
        providerCache.keySet().forEach(this::unregisterService);
        // 停止注册中心
        registryCenter.stop();
    }

    /**
     * 服务取消注册
     *
     * @param service
     */
    private void unregisterService(String service) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.unregistry(service, instance);
    }

    /**
     * 触发调用
     *
     * @param request
     * @return
     */
    public RpcResponse invoke(RpcRequest request) {
        String methodSign = request.getMethodSign();
        List<ProviderMeta> providerMetas = providerCache.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, methodSign);
            if (Objects.isNull(providerMeta)) {
                throw new IllegalArgumentException("Can't find provider meta.");
            }
            Method method = providerMeta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(providerMeta.getServiceImpl(), args);
            return RpcResponse.ofSuccess(result);
        } catch (Exception e) {
            return RpcResponse.ofFail(e.getMessage());
        }
    }

    /**
     * 处理参数
     *
     * @param args
     * @param parameterTypes
     * @return
     */
    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (Objects.isNull(args) || args.length == 0) {
            return args;
        }
        Object[] actualArgs = new Object[args.length];

        for (int index = 0; index < args.length; index++) {
            actualArgs[index] = TypeUtil.cast(args[index], parameterTypes[index]);
        }

        return actualArgs;
    }

    /**
     * 找到提供者服务者
     *
     * @param providerMetas
     * @param methodSign
     * @return
     */
    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optionalProviderMeta = providerMetas.stream().filter(providerMeta -> providerMeta.getMethodSign().equals(methodSign)).findFirst();
        return optionalProviderMeta.orElse(null);
    }
}
