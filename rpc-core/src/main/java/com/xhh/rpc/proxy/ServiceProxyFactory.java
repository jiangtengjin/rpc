package com.xhh.rpc.proxy;


import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 * 用于创建代理对象
 */
public class ServiceProxyFactory {

    public static <T> T createProxy(Class<T> serviceClazz){
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new ServiceProxy());
    }

}
