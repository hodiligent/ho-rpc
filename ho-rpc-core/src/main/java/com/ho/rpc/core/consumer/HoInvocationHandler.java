package com.ho.rpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/21 09:11
 */
public class HoInvocationHandler implements InvocationHandler {

    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private Class<?> service;

    public HoInvocationHandler(Class<?> service) {
        this.service = service;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造入参
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(this.service.getCanonicalName());
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);

        if (rpcResponse.getSuccess()) {
            JSONObject data = (JSONObject) rpcResponse.getData();
            return data.toJavaObject(method.getReturnType());
        }

        throw new RuntimeException(rpcResponse.getMgs());
    }


    private OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();
        try {
            String resJson = Objects.requireNonNull(CLIENT.newCall(request).execute().body()).string();
            return JSON.parseObject(resJson, RpcResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
