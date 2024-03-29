package com.ho.rpc.core.cluster;

import com.ho.rpc.core.api.LoadBalancer;
import com.ho.rpc.core.meta.InstanceMeta;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 轮询负载均衡
 * @Author LinJinhao
 * @Date 2024/3/22 17:27
 */
public class RoundRibonLoadBalancer implements LoadBalancer {
    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        // 将负数值调整成整数
        return providers.get((index.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
