package com.xhh.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 服务注册信息类
 */
@Data
@AllArgsConstructor
public class ServiceRegisterInfo<T> {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实现类
     */
    private Class<? extends T> implClass;

}
