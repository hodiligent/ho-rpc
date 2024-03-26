package com.ho.rpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Description provider的元数据
 * @Author LinJinhao
 * @Date 2024/3/22 09:49
 */
@Data
public class ProviderMeta {
    /**
     * 对应具体方法
     */
    private Method method;

    /**
     * 方法签名
     */
    private String methodSign;

    /**
     * 具体实现
     */
    private Object serviceImpl;
}
