package com.xhh.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 配置工具类
 * 用于读取配置文件并返回配置对象
 */
@Slf4j
public class ConfigUtil {

    private static final String[] EXTENSIONS = {"properties", "yml", "yaml"};

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
        for (String extension : EXTENSIONS) {
            String path = String.format("%s.%s", configFileBuilder, extension);
            // 依次判断文件是否存在
            if (!resourceExits(path)){
                continue;
            }
            log.info("加载配置文件: {}", path);
            switch (extension){
                case "properties":
                    // 指定字符集，可以读取中文
                    Props props = new Props(path, StandardCharsets.UTF_8);
                    // 监听配置文件的变更，自动更新配置对象
                    props.autoLoad(true);
                    return props.toBean(clazz, prefix);
                case "yml":
                case "yaml":
                    Dict dict = YamlUtil.loadByPath(path);
                    return BeanUtil.copyProperties(dict.getBean(prefix), clazz);
            }
        }
        throw new RuntimeException("不支持的配置文件格式");
    }

    /**
     * 判断资源文件是否存在
     *
     * @param path
     * @return
     */
    private static boolean resourceExits(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(path) != null;
    }

}
