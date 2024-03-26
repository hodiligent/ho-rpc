package com.ho.rpc.core.api;

import com.ho.rpc.core.meta.InstanceMeta;
import com.ho.rpc.core.meta.ServiceMeta;
import com.ho.rpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/22 17:44
 */
public interface RegistryCenter {
    /**
     * 开始运行
     */
    void start();

    /**
     * 终止运行
     */
    void stop();

    /**
     * 注册服务(provider侧)
     *
     * @param service
     * @param instance
     */
    void registry(String service, InstanceMeta instance);

    /**
     * 注销服务(provider侧)
     *
     * @param service
     * @param instance
     */
    void unRegistry(String service, InstanceMeta instance);

    /**
     * 获取所有服务(consumer侧)
     *
     * @return
     */
    List<InstanceMeta> fetchAll(ServiceMeta service);

    /**
     * 订阅服务
     *
     * @param service
     * @param changedListener
     */
    void subscribe(ServiceMeta service, ChangedListener changedListener);

    /**
     * 静态注册中心
     */
    class StaticRegistryCenter implements RegistryCenter {
        private final List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void registry(String service, InstanceMeta instance) {
            providers.add(instance);
        }

        @Override
        public void unRegistry(String service, InstanceMeta instance) {
            providers.remove(instance);
        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener changedListener) {

        }
    }
}
