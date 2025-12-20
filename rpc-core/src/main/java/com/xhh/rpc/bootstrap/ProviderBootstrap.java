package com.xhh.rpc.bootstrap;

import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.model.ServiceRegisterInfo;
import com.xhh.rpc.register.LocalRegister;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import com.xhh.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务提供者启动类
 */
@Slf4j
public class ProviderBootstrap {

    /**
     * 初始化
     *
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        // RPC 框架初始化
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String name = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegister.registryService(name, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Register register = RegisterFactory.getInstance(registryConfig.getType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setName(name);
            serviceMetaInfo.setHost(rpcConfig.getServerHost());
            serviceMetaInfo.setPort(rpcConfig.getServerPort());
            try {
                register.registry(serviceMetaInfo);
                log.info("服务：{} 注册成功！", serviceMetaInfo.getServiceAddress());
            } catch (Exception e) {
                log.error("服务：{} 注册失败！", serviceMetaInfo.getServiceAddress(), e);
                throw new RuntimeException(e);
            }
        }

        // 启动 web 服务（使用 tcp 协议）
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(rpcConfig.getServerPort());
    }
}
