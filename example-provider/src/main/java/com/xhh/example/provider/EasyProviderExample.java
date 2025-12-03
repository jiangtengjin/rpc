package com.xhh.example.provider;

import com.xhh.example.common.service.UserService;
import com.xhh.rpc.easy.register.LocalRegister;
import com.xhh.rpc.easy.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 注册服务
        LocalRegister.registryService(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }

}
