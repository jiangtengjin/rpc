package com.xhh.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.xhh.example.common.model.User;
import com.xhh.example.common.service.UserService;
import com.xhh.rpc.easy.model.RpcRequest;
import com.xhh.rpc.easy.model.RpcResponse;
import com.xhh.rpc.easy.serializer.JdkSerializer;

import java.io.IOException;

/**
 * UserService 代理类（静态代理）
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 指定序列化器
        final JdkSerializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .parameters(new Object[]{user})
                .build();
        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            byte[] result = null;
            try (HttpResponse response = HttpRequest.post("http://localhost:8080")
                            .body(serialized)
                            .execute()) {
                result = response.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
