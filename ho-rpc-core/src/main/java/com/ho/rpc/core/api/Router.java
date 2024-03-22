package com.ho.rpc.core.api;

import java.util.List;

/**
 * @Description 路由器
 * @Author LinJinhao
 * @Date 2024/3/22 16:32
 */
public interface Router<T> {

    /**
     * 选择provider集群
     *
     * @param providers
     * @return
     */
    List<T> route(List<T> providers);

    /**
     * 获取默认路由
     */
    Router Default = providers -> providers;
}
