package com.ho.rpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.ho.rpc.core.api.*;
import com.ho.rpc.core.util.MethodUtil;
import com.ho.rpc.core.util.TypeUtil;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
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

    private List<String> providers;

    private RpcContext rpcContext;

    public HoInvocationHandler(Class<?> service, List<String> providers, RpcContext rpcContext) {
        this.service = service;
        this.providers = providers;
        this.rpcContext = rpcContext;
    }


    /**
     * 发起调用
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 构造入参
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(this.service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtil.getMethodSign(method));
        rpcRequest.setArgs(args);
        // 获取路由
        List<String> urls = rpcContext.getRouter().route(this.providers);
        // 负载均衡
        String url = (String) rpcContext.getLoadBalancer().choose(urls);

        RpcResponse rpcResponse = post(rpcRequest, url);
        if (rpcResponse.getSuccess()) {
            return TypeUtil.castMethodResult(method, rpcResponse.getData());
        }

        throw new RuntimeException(rpcResponse.getMgs());
    }

    /**
     * okhttp客户端
     */
    private OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    /**
     * 发起请求
     *
     * @param rpcRequest
     * @return
     */
    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
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
