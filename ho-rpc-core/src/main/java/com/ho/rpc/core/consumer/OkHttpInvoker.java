package com.ho.rpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/26 09:20
 */
public class OkHttpInvoker implements HttpInvoker {
    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    public OkHttpInvoker() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 发起请求
     *
     * @param rpcRequest
     * @return
     */
    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();
        try {
            String resJson = Objects.requireNonNull(this.client.newCall(request).execute().body()).string();
            return JSON.parseObject(resJson, RpcResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
