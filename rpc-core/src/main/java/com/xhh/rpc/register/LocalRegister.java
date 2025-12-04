package com.xhh.rpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器
 */
public class LocalRegister {

    /**
     * 存储注册信息
     */
    private static final Map<String, Class<?>> register =  new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName       服务名称
     * @param implClass         实现类
     */
    public static void registryService(String serviceName, Class<?> implClass) {
        register.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName      服务名称
     * @return                 实现类
     */
    public static Class<?> getService(String serviceName) {
        return register.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName      服务名称
     */
    public static void removeService(String serviceName) {
        register.remove(serviceName);
    }
}
