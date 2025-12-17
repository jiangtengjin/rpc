package com.xhh.rpc;

import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.constant.RpcConstant;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import com.xhh.rpc.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的变量。双重检查锁单例模式实现。
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig 自定义配置
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Register register = RegisterFactory.getInstance(registryConfig.getType());
        register.init(registryConfig);
        log.info("register init, config = {}", registryConfig);
    }

    /**
     * 框架初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认配置
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     *
     * @return RpcConfig
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class){
                if (rpcConfig == null)
                    init();
            }
        }

        return rpcConfig;
    }

}
