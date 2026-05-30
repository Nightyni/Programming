package com.dao;

import com.pojo.UserForm;

public interface UserDao {
    // 注册用户
    int register(UserForm user);
    
    // 用户登录
    UserForm login(String username, String password);
    
    // 检查用户名是否存在
    boolean checkUsername(String username);
}