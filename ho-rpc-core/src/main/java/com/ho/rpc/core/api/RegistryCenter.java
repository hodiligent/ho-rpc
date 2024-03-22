package com.ho.rpc.core.api;

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
    void registry(String service, String instance);

    /**
     * 注销服务(provider侧)
     *
     * @param service
     * @param instance
     */
    void unregistry(String service, String instance);

    /**
     * 获取所有服务(consumer侧)
     *
     * @return
     */
    List<String> fetchAll(String service);

//    void subscribe();

    /**
     * 静态注册中心
     */
    class StaticRegistryCenter implements RegistryCenter {
        private final List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void registry(String service, String instance) {
            providers.add(instance);
        }

        @Override
        public void unregistry(String service, String instance) {
            providers.remove(instance);
        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }
    }
}
