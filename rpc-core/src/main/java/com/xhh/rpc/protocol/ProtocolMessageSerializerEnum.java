package com.xhh.rpc.protocol;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协议消息的序列化器枚举
 */
@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.value = value;
        this.key = key;
    }

    /**
     * 获取枚举的值列表
     *
     * @return  List<String>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 key 获取枚举
     *
     * @param key     key
     * @return        ProtocolMessageSerializerEnum
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum protocolMessageStatusEnum : ProtocolMessageSerializerEnum.values()) {
            if (protocolMessageStatusEnum.key == key) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value   value
     * @return        ProtocolMessageSerializerEnum
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        for (ProtocolMessageSerializerEnum protocolMessageStatusEnum : ProtocolMessageSerializerEnum.values()) {
            if (protocolMessageStatusEnum.value.equals(value)) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }

}
