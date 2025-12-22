package com.xhh.rpc.springboot.starter.annotation;

import com.xhh.rpc.RpcApplication;
import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.config.RpcConfig;
import com.xhh.rpc.model.ServiceMetaInfo;
import com.xhh.rpc.register.LocalRegister;
import com.xhh.rpc.register.Register;
import com.xhh.rpc.register.RegisterFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

public class RpcComponentScanRegister implements ImportBeanDefinitionRegistrar {
    /**
     * 扫描指定包下的类，并注册服务
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(RpcComponentScan.class.getName());
        if (attributes != null) {
            String[] packages = (String[]) attributes.get("value");

            // 扫描指定包下的类
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));

            // 注册找到的服务
            for (String pkg : packages){
                Set<BeanDefinition> beans = scanner.findCandidateComponents(pkg);
                // 注册服务
                beans.forEach(this::registryServiceBean);
            }

        }
    }

    /**
     * 注册服务
     *
     * @param beanDefinition
     */
    private void registryServiceBean(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2. 注册服务
            // 本地注册
            LocalRegister.registryService(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Register register = RegisterFactory.getInstance(registryConfig.getType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setName(serviceName);
            serviceMetaInfo.setVersion(serviceVersion);
            serviceMetaInfo.setHost(rpcConfig.getServerHost());
            serviceMetaInfo.setPort(rpcConfig.getServerPort());
            try {
                register.registry(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("service: {" + serviceName + "} registry error", e);
            }
        }
    }
}
