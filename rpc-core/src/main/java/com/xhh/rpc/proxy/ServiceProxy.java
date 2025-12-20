package com.xhh.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.constant.RpcConstant;
import com.xhh.rpc.loadbalancer.LoadBalancer;
import com.xhh.rpc.loadbalancer.LoadBalancerFactory;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import com.xhh.rpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * service (JDK 动态代理)
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        try {
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Register register = RegisterFactory.getInstance(rpcConfig.getRegistryConfig().getType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setName(rpcRequest.getServiceName());
            serviceMetaInfo.setVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceList = register.discovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceList)) {
                throw new RuntimeException("not fount available service");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedService = loadBalancer.select(requestParams, serviceList);
            log.info("selected service: {}", selectedService);

            // 发送 TCP 请求
            RpcResponse response = VertxTcpClient.doRequest(rpcRequest, selectedService);
            return response.getData();
        } catch (Exception e) {
            throw new RuntimeException("Request error", e);
        }
    }
}
