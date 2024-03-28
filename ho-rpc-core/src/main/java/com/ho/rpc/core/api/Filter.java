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
    Object before(RpcRequest rpcRequest);

    /**
     * 后置过滤
     *
     * @param rpcRequest
     * @param rpcResponse
     * @return
     */
    Object after(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result);


    Filter Default = new Filter() {
        @Override
        public Object before(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object after(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
            return result;
        }
    };
}
