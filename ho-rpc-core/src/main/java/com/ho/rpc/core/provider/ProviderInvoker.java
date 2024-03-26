package com.ho.rpc.core.provider;

import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.meta.ProviderMeta;
import com.ho.rpc.core.util.TypeUtil;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/26 09:07
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> providerCache;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.providerCache = providerBootstrap.getProviderCache();
    }

    /**
     * 触发调用
     *
     * @param request
     * @return
     */
    public RpcResponse<?> invoke(RpcRequest request) {
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
