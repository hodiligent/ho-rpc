package com.ho.rpc.core.consumer;

import com.ho.rpc.core.api.Filter;
import com.ho.rpc.core.api.LoadBalancer;
import com.ho.rpc.core.api.RegistryCenter;
import com.ho.rpc.core.api.Router;
import com.ho.rpc.core.cluster.RandomLoadBalancer;
import com.ho.rpc.core.filter.CacheFilter;
import com.ho.rpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/21 09:05
 */
public class ConsumerConfig {
    @Value("${ho-rpc.providers}")
    private String services;

    @Bean
    public ConsumerBootstrap initConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBoostrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.start();
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return new RandomLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean
    public Filter filter() {
        return new CacheFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRegistryCenter() {
        return new ZkRegistryCenter();
    }
}
