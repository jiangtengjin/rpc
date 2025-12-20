package com.xhh.rpc.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.xhh.rpc.model.RpcRequest;
import com.xhh.rpc.model.RpcResponse;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 故障转移：出现调用失败的情况后，调用其他节点的服务
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取服务列表
        List<ServiceMetaInfo> serviceList = (List<ServiceMetaInfo>) context.get("serviceList");

        // 获取请求对象
        RpcRequest request = (RpcRequest) context.get("request");

        // 获取当前服务
        ServiceMetaInfo currentService = (ServiceMetaInfo) context.get("currentService");
        // 过滤掉当前服务，获取备用服务
        serviceList = serviceList.stream().filter(service -> !service.equals(currentService)).collect(Collectors.toList());
        if (CollUtil.isEmpty(serviceList)) {
            log.error("Fail over tolerant strategy: not found available service");
            throw new RuntimeException("Fail over tolerant strategy: not found available service", e);
        }
        // 调用备用服务
        RpcResponse response = null;
        for (ServiceMetaInfo service : serviceList) {
            try {
                response = VertxTcpClient.doRequest(request, service);
                break;
            } catch (Exception ex) {
                log.error("Fail over tolerant strategy: call service {} error", service, ex);
            }
        }
        return response;
    }
}
