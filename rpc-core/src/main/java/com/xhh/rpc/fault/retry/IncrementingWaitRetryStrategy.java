package com.xhh.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.xhh.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 递增等待时长策略，指定初始等待值，然后重试间隔随次数等差递增，比如依次等待10s、30s、60s（递增为 10s）
 */
@Slf4j
public class IncrementingWaitRetryStrategy implements RetryStrategy{
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
                .withWaitStrategy(WaitStrategies.incrementingWait(10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS))
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
