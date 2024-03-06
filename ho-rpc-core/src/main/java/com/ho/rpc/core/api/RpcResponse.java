package com.ho.rpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    /**
     * 状态
     */
    private Boolean status;

    /**
     * 信息
     */
    private String mgs;

    /**
     * 数据
     */
    private T data;


    /**
     * 返回成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> ofSuccess(T data) {
        return new RpcResponse<>(true, null, data);
    }

    /**
     * 返回错误
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> ofFail(String msg) {
        return new RpcResponse<>(false, msg, null);
    }
}
