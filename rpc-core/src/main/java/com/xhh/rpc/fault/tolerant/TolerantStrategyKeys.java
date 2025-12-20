package com.xhh.rpc.fault.tolerant;

/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 失败恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 静默处理
     */
    String FAIL_SAVE = "failSave";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

}
