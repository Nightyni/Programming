package com.codebox.service;

import com.codebox.entity.User;

public interface UserService {

    // 登录
    User login(String username, String password);

    // 注册
    boolean register(User user);

    // 检查用户名是否存在
    boolean isUsernameExist(String username);
}