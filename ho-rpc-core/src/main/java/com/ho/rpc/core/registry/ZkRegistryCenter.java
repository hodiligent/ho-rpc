package com.ho.rpc.core.registry;

import com.ho.rpc.core.api.RegistryCenter;

import java.util.List;

/**
 * @Description ZK注册中心
 * @Author LinJinhao
 * @Date 2024/3/22 18:02
 */
public class ZkRegistryCenter implements RegistryCenter {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void registry(String service, String instance) {

    }

    @Override
    public void unregistry(String service, String instance) {

    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
