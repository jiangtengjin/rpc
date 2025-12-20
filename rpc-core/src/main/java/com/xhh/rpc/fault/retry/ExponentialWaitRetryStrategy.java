package com.xhh.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.xhh.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数等待时长策略，指定初始值，然后每次间隔乘 2，依次等待 2s、6s、14s；
 * 可以设置最大等待时长，达到最大等待时长后每次重试将等待最大时长。
 */
@Slf4j
public class ExponentialWaitRetryStrategy implements RetryStrategy{
    /**
     * 执行重试
     *
     * @param callable  重试任务
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.exponentialWait(5, 30, TimeUnit.SECONDS))
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
