package com.xhh.example.common.service;

import com.xhh.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user  用户
     * @return      用户
     */
    User getUser(User user);

}
