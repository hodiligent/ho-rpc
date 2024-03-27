package com.ho.rpc.core.api;

/**
 * @Description 过滤器
 * @Author LinJinhao
 * @Date 2024/3/22 16:32
 */
public interface Filter {
    /**
     * 前置过滤
     *
     * @param rpcRequest
     * @return
     */
    RpcResponse before(RpcRequest rpcRequest);

    /**
     * 后置过滤
     *
     * @param rpcRequest
     * @param rpcResponse
     * @return
     */
    RpcResponse after(RpcRequest rpcRequest, RpcResponse rpcResponse);


    Filter Default = new Filter() {
        @Override
        public RpcResponse before(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public RpcResponse after(RpcRequest rpcRequest, RpcResponse rpcResponse) {
            return rpcResponse;
        }
    };
}
