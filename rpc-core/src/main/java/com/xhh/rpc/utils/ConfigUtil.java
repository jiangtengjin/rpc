package com.xhh.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.xhh.rpc.config.RpcConfig;

/**
 * 配置工具类
 * 用于读取配置文件并返回配置对象
 */
public class ConfigUtil {

    /**
     * 加载配置对象
     *
     * @param clazz     配置对象类型
     * @param prefix    配置文件前缀
     * @return          配置对象
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix){
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param clazz         配置对象类型
     * @param prefix        配置文件前缀
     * @param environment   环境
     * @return              配置对象
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }

}
