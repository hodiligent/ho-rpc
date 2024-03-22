package com.ho.rpc.core.api;

import lombok.Data;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:16
 */
@Data
public class RpcRequest {
    /**
     * 接口路径
     */
    private String service;

    /**
     * 方法签名
     */
    private String methodSign;

    /**
     * 参数
     */
    private Object[] args;
}
