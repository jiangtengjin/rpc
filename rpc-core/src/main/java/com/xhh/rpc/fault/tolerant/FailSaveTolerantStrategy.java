package com.xhh.rpc.fault.tolerant;

import com.xhh.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理：系统出现部分非重要功能的异常时，直接忽略掉，不做任何处理。
 */
@Slf4j
public class FailSaveTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("Fail save tolerant strategy: ", e);
        return new RpcResponse();
    }
}
