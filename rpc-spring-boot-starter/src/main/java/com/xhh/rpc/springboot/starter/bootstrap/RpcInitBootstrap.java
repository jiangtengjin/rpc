package com.xhh.rpc.springboot.starter.bootstrap;

import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.server.tcp.VertxTcpServer;
import com.xhh.rpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * RPC 框架启动
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            // 启动 web 服务（使用 tcp 协议）
            VertxTcpServer server = new VertxTcpServer();
            server.doStart(rpcConfig.getServerPort());
        } else {
            log.info("RPC 框架启动成功，不需要启动服务器");
        }
    }
}
