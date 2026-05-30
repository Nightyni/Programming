-- 创建数据库
CREATE DATABASE IF NOT EXISTS task_management;
USE task_management;

-- 用户表
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 任务表
CREATE TABLE tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status ENUM('待完成', '进行中', '已完成', '已取消') DEFAULT '待完成',
    priority ENUM('低', '中', '高') DEFAULT '中',
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 插入测试数据
INSERT INTO users (username, password, email) VALUES 
('admin', 'admin123', 'admin@example.com'),
('user1', 'user123', 'user1@example.com');

INSERT INTO tasks (user_id, title, description, status, priority, due_date) VALUES 
(1, '完成课程设计', '编写Java+MySQL任务管理系统', '进行中', '高', '2025-12-28'),
(1, '学习Spring Boot', '掌握Spring Boot框架基础', '待完成', '中', '2025-12-30'),
(2, '准备期末考试', '复习云计算课程内容', '待完成', '高', '2025-12-25');

