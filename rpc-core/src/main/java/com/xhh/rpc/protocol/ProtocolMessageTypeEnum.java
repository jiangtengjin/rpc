package com.xhh.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息的类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * 根据 key 获取枚举
     *
     * @param key     key
     * @return        ProtocolMessageTypeEnum
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum protocolMessageStatusEnum : ProtocolMessageTypeEnum.values()) {
            if (protocolMessageStatusEnum.key == key) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }

}
