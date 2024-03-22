package com.ho.rpc.core.api;

import java.util.List;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 16:32
 */
public interface LoadBalancer<T> {

    /**
     * 选择一个provider
     *
     * @param providers
     * @return
     */
    T choose(List<T> providers);
}
