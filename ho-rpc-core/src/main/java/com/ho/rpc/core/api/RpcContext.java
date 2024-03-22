package com.ho.rpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 17:37
 */
@Data
public class RpcContext {
    /**
     * 过滤器
     */
    private List<Filter> filters;

    /**
     * 路由器
     */
    private Router router;

    /**
     * 负载均衡器
     */
    private LoadBalancer loadBalancer;
}
