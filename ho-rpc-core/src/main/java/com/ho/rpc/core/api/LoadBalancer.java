package com.ho.rpc.core.api;

import com.ho.rpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 16:32
 */
public interface LoadBalancer {

    /**
     * 选择一个provider
     *
     * @param providers
     * @return
     */
    InstanceMeta choose(List<InstanceMeta> providers);
}
