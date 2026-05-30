-- 删除数据库（如果存在）
DROP DATABASE IF EXISTS login_db;

-- 创建数据库
CREATE DATABASE login_db;

-- 使用数据库
USE login_db;

-- 创建用户表
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(50) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 插入测试数据
INSERT INTO user (username, password, email) VALUES 
('admin', '123456', 'admin@example.com'),
('test', '123456', 'test@example.com');

-- 查询数据
SELECT * FROM user;