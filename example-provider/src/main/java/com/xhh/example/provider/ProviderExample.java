package com.xhh.example.provider;

import com.xhh.example.common.service.UserService;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.register.LocalRegister;
import com.xhh.rpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegister.registryService(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }

}
