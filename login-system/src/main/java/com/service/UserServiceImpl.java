package com.service;

import com.dao.UserDao;
import com.pojo.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Override
    public boolean register(UserForm user) {
        // 检查用户名是否已存在
        if (userDao.checkUsername(user.getUsername())) {
            return false;
        }
        int result = userDao.register(user);
        return result > 0;
    }
    
    @Override
    public UserForm login(String username, String password) {
        return userDao.login(username, password);
    }
    
    @Override
    public boolean checkUsername(String username) {
        return userDao.checkUsername(username);
    }
}