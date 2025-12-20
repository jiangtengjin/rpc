package com.xhh.rpc.fault.tolerant;

import com.xhh.rpc.model.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     *
     * @param context   上下文，用于传递参数
     * @param e         异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;

}
