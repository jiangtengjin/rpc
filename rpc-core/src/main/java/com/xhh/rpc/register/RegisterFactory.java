package com.xhh.rpc.register;

import com.xhh.rpc.spi.SpiLoader;

/**
 * 注册中心工厂（用户创建注册中心对象）
 */
public class RegisterFactory {

    static {
        SpiLoader.load(Register.class);
    }

    /**
     * 默认注册中心
     */
    private static final Register DEFAULT_REGISTER = new EtcdRegister();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Register getInstance(String key) {
        return SpiLoader.getInstance(Register.class, key);
    }

}
