package com.ho.rpc.core.consumer;

import com.ho.rpc.core.api.Filter;
import com.ho.rpc.core.api.RpcContext;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.consumer.http.OkHttpInvoker;
import com.ho.rpc.core.meta.InstanceMeta;
import com.ho.rpc.core.util.MethodUtil;
import com.ho.rpc.core.util.TypeUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/21 09:11
 */
public class HoInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private List<InstanceMeta> providers;

    private RpcContext rpcContext;

    private HttpInvoker httpInvoker = new OkHttpInvoker();

    public HoInvocationHandler(Class<?> service, List<InstanceMeta> providers, RpcContext rpcContext) {
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

        int retries = Integer.parseInt(this.rpcContext.getParameters().getOrDefault("app.retries", "1"));

        while (retries-- > 0) {
            try {
                // 前置过滤器
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object preFilterResult = filter.before(rpcRequest);
                    if (Objects.nonNull(preFilterResult)) {
                        return preFilterResult;
                    }
                }
                // 获取路由
                List<InstanceMeta> instanceMetas = rpcContext.getRouter().route(providers);
                // 负载均衡
                InstanceMeta instanceMeta = rpcContext.getLoadBalancer().choose(instanceMetas);
                // 发起请求
                RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instanceMeta.toUrl());
                Object result = castResponse(method, rpcResponse);
                // 后置过滤器
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object postFilterResult = filter.after(rpcRequest, rpcResponse, result);
                    if (Objects.nonNull(postFilterResult)) {
                        return postFilterResult;
                    }
                }
            } catch (Exception e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    throw e;
                }
            }
        }
        // 处理相应结果
        return null;
    }

    /**
     * 处理响应结果
     *
     * @param method
     * @param rpcResponse
     * @return
     */
    private static Object castResponse(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.getSuccess()) {
            return TypeUtil.castMethodResult(method, rpcResponse.getData());
        }
        throw new RuntimeException(rpcResponse.getMsg());
    }
}
