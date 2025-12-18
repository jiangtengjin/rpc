package com.xhh.rpc.register;

import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 */
public interface Register {

    /**
     * 初始化
     *
     * @param registryConfig    注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     *
     * @param serviceMetaInfo 服务元信息
     * @throws Exception
     */
    void registry(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 心跳检测
     */
    void heartBeat();

    /**
     * 注销服务
     *
     * @param serviceMetaInfo   服务元信息
     */
    void dispose(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     *
     * @param serviceKey     服务 key
     * @return               服务列表
     */
    List<ServiceMetaInfo> discovery(String serviceKey);

    /**
     * 销毁服务
     */
    void destroy();

}
