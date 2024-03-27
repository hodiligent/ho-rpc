package com.ho.rpc.consumer;

import com.alibaba.fastjson.JSON;
import com.ho.rpc.core.annotation.HoConsumer;
import com.ho.rpc.core.consumer.ConsumerConfig;
import com.ho.rpc.model.User;
import com.ho.rpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:13
 */
@Slf4j
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
            // 常规int类型，返回User对象
            log.debug("Case 1. >>===[常规int类型，返回User对象]===");
            User user = userService.findById(1);
            log.debug("RPC result userService.findById(1) = " + user);

            // 测试方法重载，同名方法，参数不同
            log.debug("Case 2. >>===[测试方法重载，同名方法，参数不同===");
            User user1 = userService.findById(1, "hubao");
            log.debug("RPC result userService.findById(1, \"hubao\") = " + user1);

            // 测试返回字符串
            log.debug("Case 3. >>===[测试返回字符串]===");
            log.debug("userService.getName() = " + userService.getName());

            // 测试重载方法返回字符串
            log.debug("Case 4. >>===[测试重载方法返回字符串]===");
            log.debug("userService.getName(123) = " + userService.getName(123));

            // 测试local toString方法
//            log.debug();("Case 5. >>===[测试local toString方法]===");
//            log.debug();("userService.toString() = " + userService.toString());

            // 测试long类型
            log.debug("Case 6. >>===[常规int类型，返回User对象]===");
            log.debug("userService.getId(10) = " + userService.getId(10));

            // 测试long+float类型
            log.debug("Case 7. >>===[测试long+float类型]===");
            log.debug("userService.getId(10f) = " + userService.getId(10f));

            // 测试参数是User类型
            log.debug("Case 8. >>===[测试参数是User类型]===");
            log.debug("userService.getId(new User(100,\"Lin\")) = " +
                    userService.getId(new User(100, "Lin")));


            log.debug("Case 9. >>===[测试返回long[]]===");
            log.debug(" ===> userService.getLongIds(): ");
            for (long id : userService.getLongIds()) {
                log.debug(String.valueOf(id));
            }

            log.debug("Case 10. >>===[测试参数和返回值都是long[]]===");
            log.debug(" ===> userService.getLongIds(): ");
            for (long id : userService.getIds(new int[]{4, 5, 6})) {
                log.debug(String.valueOf(id));
            }

            // 测试参数和返回值都是List类型
            log.debug("Case 11. >>===[测试参数和返回值都是List类型]===");
            List<User> list = userService.getList(List.of(
                    new User(100, "Lin100"),
                    new User(101, "Lin101")));
            list.forEach(item -> log.debug(JSON.toJSONString(item)));

            // 测试参数和返回值都是Map类型
            log.debug("Case 12. >>===[测试参数和返回值都是Map类型]===");
            Map<String, User> map = new HashMap<>();
            map.put("A200", new User(200, "Lin200"));
            map.put("A201", new User(201, "Lin201"));
            userService.getMap(map).forEach(
                    (k, v) -> log.debug(k + " -> " + v)
            );

            log.debug("Case 13. >>===[测试参数和返回值都是Boolean/boolean类型]===");
            log.debug("userService.getFlag(false) = " + userService.getFlag(false));

            log.debug("Case 14. >>===[测试参数和返回值都是User[]类型]===");
            User[] users = new User[]{
                    new User(100, "Lin100"),
                    new User(101, "Lin101")};
            Arrays.stream(userService.findUsers(users)).forEach(System.out::println);

            log.debug("Case 15. >>===[测试参数为long，返回值是User类型]===");
            User userLong = userService.findById(10000L);
            log.debug(JSON.toJSONString(userLong));

            log.debug("Case 16. >>===[测试参数为boolean，返回值都是User类型]===");
            User user100 = userService.ex(false);
            log.debug(JSON.toJSONString(user100));

            log.debug("Case 17. >>===[测试服务端抛出一个RuntimeException异常]===");
            try {
                User userEx = userService.ex(true);
                log.debug(JSON.toJSONString(userEx));
            } catch (RuntimeException e) {
                log.debug(" ===> exception: " + e.getMessage());
            }
        };
    }
}
