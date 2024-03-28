package com.ho.rpc.core.filter;

import com.ho.rpc.core.api.Filter;
import com.ho.rpc.core.api.RpcRequest;
import com.ho.rpc.core.api.RpcResponse;
import com.ho.rpc.core.util.MethodUtil;
import com.ho.rpc.core.util.MockUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/28 09:15
 */
public class MockFilter implements Filter {
    @SneakyThrows
    @Override
    public Object before(RpcRequest rpcRequest) {
        Class service = Class.forName(rpcRequest.getService());
        Method method = findMethod(service, rpcRequest.getMethodSign());
        Class clazz = method.getReturnType();
        return MockUtil.mock(clazz);
    }

    private Method findMethod(Class<?> service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtil.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtil.getMethodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object after(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        return result;
    }
}
