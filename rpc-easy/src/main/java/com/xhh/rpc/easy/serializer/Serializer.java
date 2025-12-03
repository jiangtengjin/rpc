package com.xhh.rpc.easy.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param object    需要序列化的 java 对象
     * @return          序列化后的字节数组
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes     需要反序列化的字节数组
     * @param clazz     反序列化后的 java 对象的类
     * @param <T>
     * @return          反序列化后的 java 对象
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
