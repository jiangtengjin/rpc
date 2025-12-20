package com.xhh.rpc.fault.retry;

import com.xhh.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 */
public class NoRetryStrategy implements RetryStrategy{
    /**
     * 执行重试任务
     * 如果不能计算出结果，则抛出一个异常，否则返回执行结果
     *
     * @param callable  重试任务
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
