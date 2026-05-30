package com.taskmanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/task_management?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // 修改为你的密码
    
    static {
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ 找不到MySQL驱动: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("无法加载MySQL驱动", e);
        }
    }
    
    // 私有构造方法，防止实例化
    private DatabaseConnection() {}
    
    /**
     * 获取数据库连接 - 每次都创建新连接
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("✅ 创建新数据库连接成功"); // 调试时打开
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ 数据库连接失败: " + e.getMessage());
            throw e; // 抛出异常让调用者处理
        }
    }
    
    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ 数据库连接测试成功");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ 数据库连接测试失败: " + e.getMessage());
        }
        return false;
    }
    
    // 移除closeConnection方法，让每个方法自己管理连接
}



