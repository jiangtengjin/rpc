package com.xhh.rpc.register;

import com.xhh.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存
 * 支持多个服务
 */
public class RegistryMultiServiceCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();


    /**
     * 写缓存
     *
     * @param serviceKey        服务键名
     * @param newServiceCache   更新后的服务列表
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(serviceKey, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @param serviceKey    服务键名
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey){
        return serviceCache.get(serviceKey);
    }

    /**
     * 清除缓存
     *
     * @param serviceKey    服务键名
     */
    void clearCache(String serviceKey){
        serviceCache.remove(serviceKey);
    }

}
