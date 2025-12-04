package com.xhh.rpc.proxy;


import com.xhh.rpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 * 用于创建代理对象
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类型创建代理对象
     *
     * @param serviceClazz
     * @return
     * @param <T>
     */
    public static <T> T createProxy(Class<T> serviceClazz){
        // 如果开启了 mock，则直接返回 mock 代理对象
        if (RpcApplication.getRpcConfig().isMock()){
            return createMockProxy(serviceClazz);
        }

        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new ServiceProxy());
    }

    /**
     * 根据服务类型创建 Mock 代理对象
     *
     * @param serviceClazz
     * @return
     * @param <T>
     */
    public static <T> T createMockProxy(Class<T> serviceClazz){
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new MockServiceProxy());
    }

}
