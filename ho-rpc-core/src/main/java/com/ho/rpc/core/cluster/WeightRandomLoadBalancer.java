package com.ho.rpc.core.cluster;

import com.ho.rpc.core.api.LoadBalancer;
import com.ho.rpc.core.meta.InstanceMeta;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description 权重随机负载均衡
 * @Author LinJinhao
 * @Date 2024/3/22 17:32
 */
public class WeightRandomLoadBalancer implements LoadBalancer {
    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return null;
    }
}
