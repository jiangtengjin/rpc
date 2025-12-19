package com.xhh.rpc.protocol;


import com.xhh.rpc.serializer.Serializer;
import com.xhh.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param message
     * @return
     */
    public static Buffer encode(ProtocolMessage<?> message) throws IOException {
        if (message == null || message.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = message.getHeader();
        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("Serializer not found");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] serialized = serializer.serialize(message.getBody());
        // 写入 body 的长度和数据
        buffer.appendInt(serialized.length);
        buffer.appendBytes(serialized);
        return buffer;
    }

}
