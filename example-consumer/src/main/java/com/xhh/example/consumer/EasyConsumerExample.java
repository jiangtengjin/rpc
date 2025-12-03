package com.xhh.example.consumer;

import com.xhh.example.common.model.User;
import com.xhh.example.common.service.UserService;
import com.xhh.rpc.easy.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        // 需要获取 userService 的实现类对象（静态代理）
//        UserService userService = new UserServiceProxy();
        UserService userService = ServiceProxyFactory.createProxy(UserService.class);
        User user = new User();
        user.setName("张三");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("用户名：" + newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }

}
