package com.xhh.rpc.serializer;

import com.xhh.rpc.spi.SpiLoader;

/**
 * 序列化器工厂（用于创建序列化器）
 */
public class SerializerFactory {

    /**
     * 加载序列化器
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
