package com.xhh.rpc.fault.retry;

import com.xhh.rpc.model.RpcResponse;
import org.junit.Test;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse response = retryStrategy.doRetry(() -> {
                System.out.println("执行重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println("执行结果：" + response);
        } catch (Exception e) {
            System.out.println("执行异常：" + e.getMessage());
            e.printStackTrace();
        }
    }
}