package com.xhh.example.springboot.consumer;

import com.xhh.example.common.model.User;
import com.xhh.example.common.service.UserService;
import com.xhh.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test () {
        User user = new User();
        user.setName("张三");
        User result = userService.getUser(user);
        System.out.println(result.getName());
    }

}
