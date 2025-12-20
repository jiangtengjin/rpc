package com.xhh.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.xhh.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    /**
     * 执行重试
     * 当出现指定异常时，开始执行重试策略；
     * 每隔 3 秒执行一次重试，如果执行 3 次后还是失败，则停止重试，否则返回执行结果；
     *
     * @param callable  重试任务
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("第 {} 次重试", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
