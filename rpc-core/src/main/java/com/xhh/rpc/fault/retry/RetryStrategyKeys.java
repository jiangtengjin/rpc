package com.xhh.rpc.fault.retry;

/**
 * 重试策略键名常量
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定间隔重试
     */
    String FIXED_INTERVAL = "fixedInterval";

    /**
     * 随机间隔重试
     */
    String RANDOM_WAIT = "randomWait";

    /**
     * 递增等待时长重试
     */
    String INCREMENTING_WAIT = "incrementingWait";

    /**
     * 指数退避重试
     */
    String EXPONENTIAL_WAIT = "exponentialWait";

}
