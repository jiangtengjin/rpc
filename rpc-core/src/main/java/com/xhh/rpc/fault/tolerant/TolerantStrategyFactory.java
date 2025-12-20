package com.xhh.rpc.fault.tolerant;

import com.xhh.rpc.spi.SpiLoader;

/**
 * 容错策略工厂（用于获取容错策略对象）
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认重试器
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例
     *
     * @param key   配置键名
     * @return      容错策略
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
