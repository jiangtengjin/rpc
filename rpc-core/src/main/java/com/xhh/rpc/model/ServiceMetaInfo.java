package com.xhh.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务版本号
     */
    private String version = "1.0";

    /**
     * 服务主机号
     */
    private String host;

    /**
     * 服务端口号
     */
    private int port;

    /**
     * 服务分组
     */
    private String group="default";

    /**
     * 获取服务键名
     *
     * @return
     */
    public String getServiceKey() {
        // 后续可扩展服务分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", name, version);
    }

    /**
     * 获取服务注册节点键名
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), host, port);
    }

    /**
     * 获取完整服务地址
     *
     * @return
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(host, "http")) {
            return String.format("http://%s:%s", host, port);
        }
        return String.format("%s:%s", host, port);
    }


}
