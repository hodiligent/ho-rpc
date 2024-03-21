package com.ho.rpc.consumer;

import com.ho.rpc.core.annotation.HoConsumer;
import com.ho.rpc.core.consumer.ConsumerConfig;
import com.ho.rpc.model.User;
import com.ho.rpc.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:13
 */
@SpringBootApplication
@RestController
@Import({ConsumerConfig.class})
public class HoRpcConsumerApplication {
    @HoConsumer
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(HoRpcConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            User user = userService.findById(1);
            System.out.println(user);
        };
    }
}
