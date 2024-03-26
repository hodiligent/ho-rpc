package com.ho.rpc.core.consumer;

import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/26 09:20
 */
public interface HttpInvoker {
    /**
     * post请求
     *
     * @param rpcRequest
     * @param url
     * @return
     */
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
