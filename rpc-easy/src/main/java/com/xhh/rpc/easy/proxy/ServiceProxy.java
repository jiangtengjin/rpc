package com.xhh.rpc.easy.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.xhh.example.common.model.User;
import com.xhh.example.common.service.UserService;
import com.xhh.rpc.easy.model.RpcRequest;
import com.xhh.rpc.easy.model.RpcResponse;
import com.xhh.rpc.easy.serializer.JdkSerializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * service (JDK 动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        JdkSerializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcRequest);
            // 发送请求
            // todo：注意，这里地址被硬编码了，需要使用注册中心和服务发现机制解决
            try (HttpResponse response = HttpRequest.post("http://localhost:8080")
                    .body(serialized)
                    .execute()) {
                byte[] result = response.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
