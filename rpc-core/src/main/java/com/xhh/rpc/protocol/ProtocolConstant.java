package com.xhh.rpc.protocol;

/**
 * 协议常量
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本
     */
    byte PROTOCOL_VERSION = 0x1;

    // ================= 消息头相关索引 ====================
    int MAGIC_INDEX = 0;
    int VERSION_INDEX = 1;
    int SERIALIZER_INDEX = 2;
    int TYPE_INDEX = 3;
    int STATUS_INDEX = 4;
    int REQUEST_ID_INDEX = 5;
    int BODY_LENGTH_INDEX = 13;


}
