package com.ho.rpc.core.filter;

import com.ho.rpc.core.api.Filter;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 缓存过滤器
 * @Author LinJinhao
 * @Date 2024/3/27 09:47
 */
public class CacheFilter implements Filter {
    private final static Map<String, Object> CACHE = new ConcurrentHashMap<>();

    @Override
    public Object before(RpcRequest rpcRequest) {
        return CACHE.get(rpcRequest.toString());
    }

    @Override
    public Object after(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        CACHE.putIfAbsent(rpcRequest.toString(), result);
        return result;
    }
}
