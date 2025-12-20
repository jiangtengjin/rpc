package com.xhh.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.xhh.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 随机等待时长策略，每次重试等待指定区间的时长
 */
@Slf4j
public class RandomWaitRetryStrategy implements RetryStrategy{
    /**
     * 重试策略
     * 当出现异常时执行重试任务，每次重试等待时长为 [10, 30] 区间随机值（单位秒）；
     * 重试次数为 3 次，超过 3 次则不再重试；
     *
     * @param callable  重试任务
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.randomWait(10, TimeUnit.SECONDS, 30, TimeUnit.SECONDS))
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
