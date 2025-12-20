package com.xhh.rpc.fault.tolerant;

import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 失败自动恢复：系统某个功能出现调用失败时，获取降级的服务并调用
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取降级接口
        RpcRequest request = (RpcRequest) context.get("request");
        String serviceName = request.getServiceName();
        Class<?> implClass = Class.forName(serviceName);

        // 调用模拟接口
        Object mockProxy = ServiceProxyFactory.createMockProxy(implClass);
        Method method = mockProxy.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        Object result = method.invoke(mockProxy, request.getParameters());
        // 返回结果
        RpcResponse response = new RpcResponse();
        response.setData(result);
        response.setDataType(method.getReturnType());
        response.setMessage("Fail back tolerant strategy: " + serviceName + " is not available, use mock service");
        return response;
    }
}
