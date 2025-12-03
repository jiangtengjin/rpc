package com.xhh.example.provider;

import com.xhh.example.common.model.User;
import com.xhh.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
