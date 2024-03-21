package com.ho.rpc.core.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/21 09:05
 */
public class ConsumerConfig {

    @Bean
    public ConsumerBootstrap initConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBoostrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.start();
    }
}
