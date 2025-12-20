package com.xhh.rpc.fault.tolerant;

import com.xhh.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败策略，遇到异常后将异常抛出，交由上层处理
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("Fast fail strategy: throw exception", e);
    }
}
