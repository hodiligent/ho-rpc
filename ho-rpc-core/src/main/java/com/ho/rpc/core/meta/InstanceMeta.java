package com.ho.rpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Description 描述实例的元数据
 * @Author LinJinhao
 * @Date 2024/3/22 09:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {
    /**
     * 协议
     */
    private String scheme;

    /**
     * host
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 在线
     */
    private boolean online;

    /**
     * 参数
     */
    private Map<String, String> parameters;

    /**
     * 上下文
     */
    private String context;

    public InstanceMeta(String host, Integer port) {
        this.host = host;
        this.port = port;
    }


    public String toPath() {
        return String.format("%s:%d", host, port);
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta(host, port);
    }
}
