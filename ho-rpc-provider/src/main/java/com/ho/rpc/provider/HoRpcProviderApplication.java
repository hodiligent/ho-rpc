package com.ho.rpc.provider;

import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.provider.ProviderConfig;
import com.ho.rpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/7 00:13
 */
@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class HoRpcProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoRpcProviderApplication.class, args);
    }

    @Autowired
    private ProviderInvoker providerInvoker;

    @RequestMapping("/")
    public RpcResponse<?> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
