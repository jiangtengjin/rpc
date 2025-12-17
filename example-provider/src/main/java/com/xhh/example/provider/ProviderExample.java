package com.xhh.example.provider;

import com.xhh.example.common.service.UserService;
import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.register.LocalRegister;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import com.xhh.rpc.server.HttpServer;
import com.xhh.rpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String name = UserService.class.getName();
        LocalRegister.registryService(name, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Register register = RegisterFactory.getInstance(registryConfig.getType());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName(name);
        serviceMetaInfo.setHost(rpcConfig.getServerHost());
        serviceMetaInfo.setPort(rpcConfig.getServerPort());
        try {
            register.registry(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(rpcConfig.getServerPort());
    }

}
