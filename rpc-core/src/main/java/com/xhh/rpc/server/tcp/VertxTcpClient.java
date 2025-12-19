package com.xhh.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class VertxTcpClient {

    /**
     * Vertx TCP 请求客户端
     *
     * @param rpcRequest        请求对象
     * @param serviceMetaInfo   服务元信息
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getPort(), serviceMetaInfo.getHost(), res -> {
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

        return response;
    }

}
