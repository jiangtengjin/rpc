package com.xhh.rpc.register;

import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegisterTest {

    final Register register = new EtcdRegister();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        register.init(registryConfig);
    }

    @Test
    public void testRegistry() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName("myService");
        serviceMetaInfo.setHost("localhost");
        serviceMetaInfo.setPort(1234);
        serviceMetaInfo.setVersion("1.0");
        register.registry(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName("myService");
        serviceMetaInfo.setHost("localhost");
        serviceMetaInfo.setPort(1235);
        serviceMetaInfo.setVersion("1.0");
        register.registry(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName("myService");
        serviceMetaInfo.setHost("localhost");
        serviceMetaInfo.setPort(1234);
        serviceMetaInfo.setVersion("2.0");
        register.registry(serviceMetaInfo);
    }

    @Test
    public void testDispose() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName("myService");
        serviceMetaInfo.setHost("localhost");
        serviceMetaInfo.setPort(1234);
        serviceMetaInfo.setVersion("1.0");
        register.dispose(serviceMetaInfo);
    }

    @Test
    public void testDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName("myService");
        serviceMetaInfo.setVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceList = register.discovery(serviceKey);
        Assert.assertNotNull(serviceList);
    }

}