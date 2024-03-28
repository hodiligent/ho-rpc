package com.ho.rpc.core.api;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 额外参数
     */
    private Map<String, String> parameters;

    public RpcContext() {
        this.parameters = new HashMap<>();
    }


    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }
}
