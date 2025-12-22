package com.xhh.rpc.springboot.starter.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * RPC 框架的扫描注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcComponentScanRegister.class)
public @interface RpcComponentScan {

    /**
     * 要扫描的包
     */
    String[] value() default {};

}
