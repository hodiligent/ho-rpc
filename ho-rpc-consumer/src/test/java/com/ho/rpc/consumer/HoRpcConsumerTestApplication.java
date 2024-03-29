package com.ho.rpc.consumer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/29 09:23
 */
@SpringBootTest
public class HoRpcConsumerTestApplication {

    private static ApplicationContext applicationContext;

    @BeforeAll
    public static void init() {
        applicationContext = SpringApplication.run(HoRpcConsumerApplication.class, "--server.port=8084", "--logging.level.com.ho.rpc=debug");
    }

    @Test
    public void contextLoads() {
        System.out.println("contextLoads");
    }

    @AfterAll
    public static void destroy() {
        SpringApplication.exit(applicationContext, () -> 1);
    }
}
