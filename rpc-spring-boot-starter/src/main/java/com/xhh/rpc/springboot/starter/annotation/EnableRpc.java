package com.xhh.rpc.springboot.starter.annotation;

import com.xhh.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.xhh.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.xhh.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * RPC 框架启动注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RpcComponentScan // 包扫描注解
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     */
    boolean needServer() default true;

    /**
     * 继承 RpcComponentScan 的 value 属性
     */
    @AliasFor(
            annotation = RpcComponentScan.class,
            attribute = "value"
    )
    String[] value() default {};
}
