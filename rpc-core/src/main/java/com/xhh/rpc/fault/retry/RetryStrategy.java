package com.xhh.rpc.fault.retry;

import com.xhh.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 重试
     *
     * @param callable  重试任务
     * @return          执行结果
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}
