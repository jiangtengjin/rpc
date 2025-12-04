package com.xhh.rpc.server;

import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.register.LocalRegister;
import com.xhh.rpc.serializer.JdkSerializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * http 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    /**
     * 处理器核心流程：
     * 1. 反序列化请求对象
     * 2. 根据服务名称从本地服务注册器获取服务
     * 3. 通过反射机制调用方法，得到返回结果
     * 4. 对返回结果进行反序列，并写入到响应中
     *
     * @param request
     */
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final JdkSerializer serializer = new JdkSerializer();

        // 记录日志
        System.out.println("Request received: " + request.path() + " " + request.uri());

        // 异步处理 http 请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为 null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("Request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> service = LocalRegister.getService(rpcRequest.getServiceName());
                Method method = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(service.newInstance(), rpcRequest.getParameters());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 对响应结果进行序列化，并写入到响应中
     *
     * @param request           http 请求对象
     * @param rpcResponse       rpc 响应
     * @param serializer        序列化器
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, JdkSerializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");

        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
