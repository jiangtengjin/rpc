package com.xhh.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息的状态枚举
 */
@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("bad request", 40),
    BAD_RESPONSE("bad response", 50);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value     value
     * @return          ProtocolMessageStatusEnum
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum protocolMessageStatusEnum : ProtocolMessageStatusEnum.values()) {
            if (protocolMessageStatusEnum.value == value) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }

}
