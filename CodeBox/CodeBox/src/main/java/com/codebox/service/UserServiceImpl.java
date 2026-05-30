package com.codebox.service.impl;

import com.codebox.entity.User;
import com.codebox.mapper.UserMapper;
import com.codebox.service.UserService;
import com.codebox.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        String encryptedPwd = MD5Util.encrypt(password);
        return userMapper.findByUsernameAndPassword(username, encryptedPwd);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (isUsernameExist(user.getUsername())) {
            return false;
        }
        // 密码加密
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean isUsernameExist(String username) {
        return userMapper.checkUsername(username) > 0;
    }
}