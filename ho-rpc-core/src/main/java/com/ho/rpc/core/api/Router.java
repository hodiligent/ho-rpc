package com.ho.rpc.core.api;

import com.ho.rpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @Description 路由器
 * @Author LinJinhao
 * @Date 2024/3/22 16:32
 */
public interface Router {

    /**
     * 选择provider集群
     *
     * @param providers
     * @return
     */
    List<InstanceMeta> route(List<InstanceMeta> providers);

    /**
     * 获取默认路由
     */
    Router Default = providers -> providers;
}
