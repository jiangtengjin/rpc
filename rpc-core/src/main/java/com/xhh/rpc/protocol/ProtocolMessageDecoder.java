package com.xhh.rpc.protocol;

import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.serializer.Serializer;
import com.xhh.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {

    // ================= 消息头相关索引 ====================
    private static final int MAGIC_INDEX = 0;
    private static final int VERSION_INDEX = 1;
    private static final int SERIALIZER_INDEX = 2;
    private static final int TYPE_INDEX = 3;
    private static final int STATUS_INDEX = 4;
    private static final int REQUEST_ID_INDEX = 5;
    private static final int BODY_LENGTH_INDEX = 13;

    /**
     * 解码
     *
     * @param buffer
     * @return
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 分别从指定位置读出 buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(MAGIC_INDEX);
        // 校验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("Illegal magic of message");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(VERSION_INDEX));
        header.setSerializer(buffer.getByte(SERIALIZER_INDEX));
        header.setType(buffer.getByte(TYPE_INDEX));
        header.setStatus(buffer.getByte(STATUS_INDEX));
        header.setRequestId(buffer.getLong(REQUEST_ID_INDEX));
        header.setBodyLength(buffer.getInt(BODY_LENGTH_INDEX));
        // 解决粘包问题，只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(ProtocolConstant.MESSAGE_HEADER_LENGTH,
                ProtocolConstant.MESSAGE_HEADER_LENGTH + header.getBodyLength());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("Illegal serializer of message");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (typeEnum == null) {
            throw new RuntimeException("Illegal type of message");
        }
        switch (typeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("Not supported message type");
        }
    }

}
