package com.xhh.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.constant.RpcConstant;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.protocol.*;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(selectedService.getPort(), selectedService.getHost(), res -> {
            if (res.succeeded()) {
                log.info("Connected to TCP server");
                NetSocket socket = res.result();
                // 发送消息
                // 构造消息
                ProtocolMessage<RpcRequest> message = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) ProtocolMessageSerializerEnum
                        .getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                header.setBodyLength(0);
                message.setHeader(header);
                message.setBody(rpcRequest);
                // 编码请求
                try {
                    Buffer encodeBuffer = ProtocolMessageEncoder.encode(message);
                    socket.write(encodeBuffer);
                } catch (IOException e) {
                    throw new RuntimeException("Encode message of protocol error", e);
                }
                // 接收响应
                socket.handler(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> responseProtocolMessage =
                                (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("Decode message of protocol error", e);
                    }
                });
            } else {
                log.error("Failed to connect to TCP server: " + res.cause().getMessage());
            }
        });
        RpcResponse response = responseFuture.get();
        // 关闭连接
        netClient.close();
        return response.getData();
    }
}
