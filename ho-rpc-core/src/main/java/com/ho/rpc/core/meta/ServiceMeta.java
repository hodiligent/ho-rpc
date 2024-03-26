package com.ho.rpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * @Description 描述服务元数据
 * @Author LinJinhao
 * @Date 2024/3/26 09:57
 */
@Data
@Builder
public class ServiceMeta {
    /**
     * 应用组
     */
    private String app;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 环境
     */
    private String env;

    /**
     * 服务名称
     */
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, namespace);
    }
}
