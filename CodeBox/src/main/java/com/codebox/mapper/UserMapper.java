package com.codebox.mapper;

import com.codebox.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    // 根据用户名查询用户
    User findByUsername(@Param("username") String username);

    // 根据用户名和密码查询（登录）
    User findByUsernameAndPassword(@Param("username") String username,
                                    @Param("password") String password);

    // 注册新用户
    int insert(User user);

    // 检查用户名是否存在
    int checkUsername(@Param("username") String username);
}