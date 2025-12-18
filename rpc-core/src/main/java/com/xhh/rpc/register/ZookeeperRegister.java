package com.xhh.rpc.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.xhh.rpc.config.RegistryConfig;
import com.xhh.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Zookeeper 注册中心
 */
@Slf4j
public class ZookeeperRegister implements Register{

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();


    /**
     * 注册中心服务本地缓存（支持多个服务缓存）
     */
    private final RegistryMultiServiceCache registryMultiServiceCache = new RegistryMultiServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void watch(String serviceNodeKey) {
        String registerKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newNode = watchingKeySet.add(registerKey);
        if (newNode) {
            CuratorCache curatorCache = CuratorCache.build(client, registerKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener.builder()
                            .forDeletes(childData -> registryMultiServiceCache.clearCache(serviceNodeKey))
                            .forChanges((childData, node) -> registryMultiServiceCache.clearCache(serviceNodeKey))
                            .build()
            );
        }
    }

    @Override
    public void init(RegistryConfig registryConfig) {
        // 构建 client 实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();
        // 构建 serviceDiscovery 实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try {
            // 启动
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registry(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 注册到 zk
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // 添加节点到本地缓存
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void heartBeat() {
        // 不需要心跳机制，建立了临时节点，如果服务器故障，临时节点会直接丢失
    }

    @Override
    public void dispose(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 移除缓存
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> discovery(String serviceKey) {
        // 优先从缓存中获取
        List<ServiceMetaInfo> serviceCache = registryMultiServiceCache.readCache(serviceKey);
        if (serviceCache != null) {
            return serviceCache;
        }
        // 缓存为空，从 zk 中获取
        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);
            // 解析服务信息
            List<ServiceMetaInfo> serviceList = serviceInstances.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            // 写缓存
            registryMultiServiceCache.writeCache(serviceKey, serviceList);
            return serviceList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线。。。。");
        // 下线节点（这一步可以不做，因为都是临时节点，服务下线，自然就被删除了）
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (client != null) {
            client.close();
        }
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getHost() + ":" + serviceMetaInfo.getPort();
        try {
            return ServiceInstance.<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
