package com.ho.rpc.core.provider;

import com.ho.rpc.core.api.RegistryCenter;
import com.ho.rpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Description 将ProviderBootstrap加入Spring容器
 * @Author LinJinhao
 * @Date 2024/3/7 00:33
 */
@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootstrap initProviderBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBoostrapRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> providerBootstrap.start();
    }

    @Bean
    public RegistryCenter providerRegistryCenter() {
        return new ZkRegistryCenter();
    }
}
