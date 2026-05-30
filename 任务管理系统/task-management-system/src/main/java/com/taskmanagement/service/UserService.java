package com.taskmanagement.service;

import com.taskmanagement.dao.UserDAO;
import com.taskmanagement.model.User;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * 用户注册
     */
    public boolean register(String username, String password, String email) {
        // 输入验证
        if (password == null || password.length() < 6) {
            System.out.println("❌ 密码长度至少6位");
            return false;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 注意：实际应该加密
        user.setEmail(email);
        
        return userDAO.registerUser(user);
    }
    
    /**
     * 用户登录
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.out.println("❌ 用户名和密码不能为空");
            return null;
        }
        
        return userDAO.login(username, password);
    }
}