package com.xhh.rpc.server.tcp;

import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.protocol.*;
import com.xhh.rpc.register.LocalRegister;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * tcp 请求处理器
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接收请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("Decode message of protocol error", e);
            }
            RpcRequest request = protocolMessage.getBody();

            // 处理请求
            // 构造响应结果对象
            RpcResponse response = new RpcResponse();
            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegister.getService(request.getServiceName());
                Method method = implClass.getMethod(request.getMethodName(), request.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), request.getParameters());
                // 封装返回结果
                response.setData(result);
                response.setDataType(method.getReturnType());
                response.setMessage(ProtocolMessageStatusEnum.OK.getText());
            } catch (Exception e) {
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setException(e);
            }

            // 发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, response);
            try {
                Buffer encodeBuffer = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encodeBuffer);
            } catch (IOException e) {
                throw new RuntimeException("Encode message of protocol error", e);
            }
        });
        socket.handler(bufferHandlerWrapper);
    }
}
