package com.ho.rpc.core.cluster;

import com.ho.rpc.core.api.LoadBalancer;
import com.ho.rpc.core.meta.InstanceMeta;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @Description 随机负载均衡
 * @Author LinJinhao
 * @Date 2024/3/22 17:24
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        Random random = new Random();
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
