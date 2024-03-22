package com.ho.rpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
