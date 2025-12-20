package com.xhh.rpc.loadbalancer;

import com.xhh.rpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 负载均衡器测试
 */
public class LoadBalancerTest {

    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void testSelect() {
        // 请求参数
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "apple");

        // 服务列表
        ServiceMetaInfo service = new ServiceMetaInfo();
        service.setName("myService");
        service.setVersion("1.0");
        service.setHost("localhost");
        service.setPort(1234);

        ServiceMetaInfo service1 = new ServiceMetaInfo();
        service.setName("myService");
        service.setVersion("1.0");
        service.setHost("xhhhh.cloud");
        service.setPort(80);

        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(service, service1);
        // 连续调用 3 次
        for (int i = 0; i < 3; i++) {
            ServiceMetaInfo selected = loadBalancer.select(requestParams, serviceMetaInfoList);
            Assert.assertNotNull(selected);
            System.out.println("选择的服务：" + selected);
        }
    }
}