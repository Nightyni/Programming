package com.test;

import com.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest {
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("正在启动Spring容器...");
            System.out.println("========================================\n");
            
            // 加载Spring配置
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            System.out.println("✓ Spring容器启动成功！\n");
            
            // 测试UserService
            System.out.println("测试UserService...");
            UserService userService = context.getBean(UserService.class);
            System.out.println("✓ UserService加载成功\n");
            
            // 测试数据库连接
            System.out.println("测试数据库连接...");
            com.pojo.UserForm user = userService.login("admin", "123456");
            if (user != null) {
                System.out.println("✓ 数据库连接成功！");
                System.out.println("  测试用户: " + user.getUsername());
                System.out.println("  邮箱: " + user.getEmail());
            } else {
                System.out.println("⚠ 数据库连接成功，但admin用户不存在");
                System.out.println("  请先执行SQL创建测试数据");
            }
            
            System.out.println("\n========================================");
            System.out.println("所有测试通过！");
            
        } catch (Exception e) {
            System.out.println("✗ 启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}