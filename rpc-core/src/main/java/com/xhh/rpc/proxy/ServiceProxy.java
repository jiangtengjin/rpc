package com.xhh.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.constant.RpcConstant;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import com.xhh.rpc.serializer.Serializer;
import com.xhh.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * service (JDK 动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

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

            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Register register = RegisterFactory.getInstance(rpcConfig.getRegistryConfig().getType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setName(rpcRequest.getServiceName());
            serviceMetaInfo.setVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceList = register.discovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceList)) {
                throw new RuntimeException("没有发现可用的服务");
            }
            // 暂时先取第一个
            ServiceMetaInfo selectedService = serviceList.get(0);

            // 发送请求
            try (HttpResponse response = HttpRequest.post(selectedService.getServiceAddress())
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
