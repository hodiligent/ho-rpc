package com.ho.rpc.core.provider;

import com.ho.rpc.core.annotation.HoProvider;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.meta.ProviderMeta;
import com.ho.rpc.core.util.MethodUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
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

    private MultiValueMap<String, ProviderMeta> providerCache = new LinkedMultiValueMap<>();

    @PostConstruct
    public void buildProviders() {
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
            Object result = method.invoke(providerMeta.getServiceImpl(), request.getArgs());
            return RpcResponse.ofSuccess(result);
        } catch (Exception e) {
            return RpcResponse.ofFail(e.getMessage());
        }
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
