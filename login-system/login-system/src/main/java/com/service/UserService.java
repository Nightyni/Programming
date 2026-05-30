package com.service;

import com.pojo.UserForm;

public interface UserService {
    // 注册
    boolean register(UserForm user);
    
    // 登录
    UserForm login(String username, String password);
    
    // 检查用户名是否存在
    boolean checkUsername(String username);
}