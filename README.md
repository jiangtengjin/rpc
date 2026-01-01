# RPC 框架

一个基于 Java 的轻量级、高性能 RPC 框架，支持多种序列化方式、注册中心、负载均衡算法和容错机制，提供简洁的注解驱动开发体验。

## 核心功能

1. **Web 服务器**：基于 Vert.x 实现的高性能 Web 服务器
2. **多序列化器支持**：实现了 JSON、Kryo、Hessian 等多种序列化器
3. **灵活的注册中心**：
   - 支持基于 Etcd 和 Zookeeper 的注册中心
   - 心跳检测和续期机制
   - 服务下线机制
   - 消费端服务缓存机制
4. **高效自定义协议**：
   - 基于 TCP 协议的自定义消息结构
   - 仅 17 字节的请求头，包含魔数、版本号、序列化方式、类型和状态等字段
   - 高效的字节数组传输，编解码简单快速
5. **多种负载均衡算法**：
   - 轮询算法
   - 随机算法
   - 一致性 Hash 算法
6. **灵活的重试机制**：基于 Guava-Retrying 实现
   - 固定时间重试
   - 指数退避重试
   - 随机延迟重试
   - 不重试策略
7. **强大的容错机制**：
   - 快速失败
   - 静默处理
   - 故障转移
   - 失败自动恢复
8. **注解驱动开发**：仅需三个注解即可快速使用
   - `@EnableRpc`：启动 RPC 服务
   - `@RpcReference`：用于服务消费者获取服务
   - `@RpcService`：用于服务提供者注册服务

## 项目结构

```
rpc/
├── example-common/           # 公共示例接口定义
├── example-consumer/         # 普通消费者示例
├── example-provider/         # 普通提供者示例
├── example-springboot-consumer/  # Spring Boot 消费者示例
├── example-springboot-provider/  # Spring Boot 提供者示例
├── rpc-core/                 # 核心功能实现
├── rpc-easy/                 # 简化版 RPC 实现
└── rpc-spring-boot-starter/  # Spring Boot  starters
```

## 快速开始

### 环境要求

- JDK 11+  
- Maven 3.6+  
- Etcd 或 Zookeeper（可选，用于服务注册发现）

### 简单示例

#### 1. 定义服务接口

```java
public interface UserService {
    User getUserById(Long id);
}
```

#### 2. 实现服务

```java
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("test");
        return user;
    }
}
```

#### 3. 启动服务提供者

```java
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动服务器
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(8080);
    }
}
```

#### 4. 服务消费者调用

```java
public class EasyConsumerExample {
    public static void main(String[] args) {
        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        // 调用服务
        User user = userService.getUserById(1L);
        System.out.println(user);
    }
}
```

## Spring Boot 示例

### 服务提供者

#### 1. 配置文件

```yaml
rpc:
  server:
    port: 8080
  registry:
    type: etcd
    address: http://localhost:2379
  serializer: kryo
```

#### 2. 启动类

```java
@SpringBootApplication
@EnableRpc
public class ExampleSpringbootProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }
}
```

#### 3. 服务实现

```java
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("test");
        return user;
    }
}
```

### 服务消费者

#### 1. 配置文件

```yaml
rpc:
  registry:
    type: etcd
    address: http://localhost:2379
  serializer: kryo
```

#### 2. 启动类

```java
@SpringBootApplication
@EnableRpc
public class ExampleSpringbootConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }
}
```

#### 3. 服务调用

```java
@Service
public class ExampleServiceImpl implements ExampleService {
    
    @RpcReference
    private UserService userService;
    
    @Override
    public User getUserById(Long id) {
        return userService.getUserById(id);
    }
}
```

## 注解说明

### @EnableRpc

用于启动 RPC 服务，通常添加在 Spring Boot 启动类上。

### @RpcService

用于服务提供者注册服务，添加在服务实现类上。

### @RpcReference

用于服务消费者获取服务，添加在需要调用远程服务的字段上。

## 配置选项

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| rpc.server.port | 服务端口 | 8080 |
| rpc.registry.type | 注册中心类型（etcd/zookeeper） | etcd |
| rpc.registry.address | 注册中心地址 | http://localhost:2379 |
| rpc.serializer | 序列化器类型（json/kryo/hessian） | json |
| rpc.loadbalancer | 负载均衡算法（roundRobin/random/consistentHash） | roundRobin |
| rpc.retry.strategy | 重试策略（fixed/exponential/random/no） | no |
| rpc.retry.max-attempts | 最大重试次数 | 3 |
| rpc.retry.initial-interval | 初始重试间隔（毫秒） | 1000 |
| rpc.tolerant.strategy | 容错策略（failFast/failSave/failOver/failBack） | failFast |

## 扩展机制

框架支持 SPI 机制，开发者可以自定义以下组件：

1. 序列化器：实现 `com.xhh.rpc.serializer.Serializer` 接口
2. 注册中心：实现 `com.xhh.rpc.register.Register` 接口
3. 负载均衡器：实现 `com.xhh.rpc.loadbalancer.LoadBalancer` 接口
4. 重试策略：实现 `com.xhh.rpc.fault.retry.RetryStrategy` 接口
5. 容错策略：实现 `com.xhh.rpc.fault.tolerant.TolerantStrategy` 接口

### 自定义扩展示例

1. 创建自定义实现类
2. 在 `META-INF/rpc/system/` 目录下创建对应接口的文件
3. 在文件中添加自定义实现类的全限定名

## 架构设计

### 整体架构

```
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│               │      │               │      │               │
│  服务消费者   │──────►  注册中心     │◄─────┤  服务提供者   │
│               │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│               │      │               │      │               │
│  代理层       │      │  协议层       │      │  服务层       │
│               │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│               │      │               │      │               │
│  负载均衡     │      │  序列化层     │      │  注解处理     │
│               │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│               │      │               │      │               │
│  重试机制     │      │  网络传输层   │      │  配置管理     │
│               │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│               │      │               │      │               │
│  容错机制     │      │  Vert.x 服务器│      │  SPI 扩展     │
│               │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
```

### 核心流程

1. **服务注册**：服务提供者通过 `@RpcService` 注解注册服务到注册中心
2. **服务发现**：服务消费者通过 `@RpcReference` 注解从注册中心获取服务列表
3. **负载均衡**：根据配置的负载均衡算法选择一个服务实例
4. **远程调用**：
   - 序列化请求参数
   - 通过自定义协议发送请求
   - 服务端处理请求并返回结果
   - 客户端反序列化响应结果
5. **重试与容错**：根据配置的重试策略和容错策略处理调用异常

## 技术栈

- **Java**：11+
- **Spring Boot**：2.7.x
- **Vert.x**：4.x（Web 服务器）
- **Etcd**：3.x（注册中心）
- **Zookeeper**：3.x（注册中心）
- **Guava-Retrying**：2.0（重试机制）
- **Jackson**：2.x（JSON 序列化）
- **Kryo**：5.x（Kryo 序列化）
- **Hessian**：4.x（Hessian 序列化）