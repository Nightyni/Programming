-- 创建数据库
DROP DATABASE IF EXISTS university_project_management;
CREATE DATABASE university_project_management;
USE university_project_management;

-- ============================================
-- 1. 创建表结构
-- ============================================

-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    real_name VARCHAR(50),
    role ENUM('admin', 'teacher', 'student') DEFAULT 'student',
    department VARCHAR(100),
    title VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 项目表
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(200) NOT NULL,
    project_number VARCHAR(50) UNIQUE,
    leader_id INT,
    budget DECIMAL(10,2),
    start_date DATE,
    end_date DATE,
    status ENUM('进行中', '已结题', '申请中') DEFAULT '申请中',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (leader_id) REFERENCES users(id)
);

-- 项目成员表
CREATE TABLE project_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT,
    user_id INT,
    role_in_project VARCHAR(50),
    join_date DATE,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 论文表
CREATE TABLE papers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500) NOT NULL,
    author_id INT,
    co_authors TEXT,
    journal VARCHAR(200),
    publish_date DATE,
    volume VARCHAR(20),
    issue VARCHAR(20),
    pages VARCHAR(20),
    doi VARCHAR(100),
    level ENUM('SCI', 'EI', '核心', '一般') DEFAULT '一般',
    project_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- 团队表
CREATE TABLE teams (
    id INT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(100) NOT NULL,
    leader_id INT,
    established_date DATE,
    research_area VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (leader_id) REFERENCES users(id)
);

-- 团队成员表
CREATE TABLE team_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    team_id INT,
    user_id INT,
    join_date DATE,
    position VARCHAR(50),
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================
-- 2. 创建索引（提升性能）
-- ============================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_projects_leader ON projects(leader_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_papers_author ON papers(author_id);
CREATE INDEX idx_papers_level ON papers(level);
CREATE INDEX idx_teams_leader ON teams(leader_id);


-- 查看所有表
SHOW TABLES;

-- 查看用户表结构
DESC users;

-- 查看项目表结构
DESC projects;
-- ============================================
-- 3. 插入测试数据
-- ============================================

-- 插入用户数据
INSERT INTO users (username, password, email, real_name, role, department, title) VALUES 
('admin', '123456', 'admin@example.com', '系统管理员', 'admin', '计算机学院', '教授'),
('teacher1', '123456', 'teacher1@example.com', '张教授', 'teacher', '软件学院', '教授'),
('teacher2', '123456', 'teacher2@example.com', '李教授', 'teacher', '计算机学院', '副教授'),
('student1', '123456', 'student1@example.com', '王同学', 'student', '计算机学院', NULL),
('student2', '123456', 'student2@example.com', '赵同学', 'student', '软件学院', NULL);

-- 插入项目数据
INSERT INTO projects (project_name, project_number, leader_id, budget, start_date, end_date, status) VALUES 
('基于人工智能的图像识别研究', 'KY2024001', 2, 500000.00, '2024-01-01', '2026-12-31', '进行中'),
('大数据分析平台建设', 'KY2024002', 3, 800000.00, '2024-03-01', '2025-12-31', '进行中'),
('云计算安全研究', 'KY2024003', 2, 300000.00, '2024-02-01', '2024-12-31', '申请中');

-- 插入项目成员数据
INSERT INTO project_members (project_id, user_id, role_in_project, join_date) VALUES 
(1, 2, '负责人', '2024-01-01'),
(1, 4, '研究生', '2024-01-15'),
(2, 3, '负责人', '2024-03-01'),
(2, 5, '研究生', '2024-03-15');

-- 插入论文数据
INSERT INTO papers (title, author_id, journal, publish_date, level, project_id) VALUES 
('深度学习在图像识别中的应用', 2, '计算机学报', '2024-06-15', '核心', 1),
('基于大数据的用户行为分析', 3, '软件学报', '2024-08-20', 'EI', 2),
('云计算安全机制研究', 2, '通信学报', '2024-09-10', '核心', 3);

-- 插入团队数据
INSERT INTO teams (team_name, leader_id, established_date, research_area) VALUES 
('人工智能研究团队', 2, '2023-01-01', '人工智能、机器学习'),
('大数据研究团队', 3, '2023-03-01', '大数据分析');

-- 插入团队成员数据
INSERT INTO team_members (team_id, user_id, join_date, position) VALUES 
(1, 2, '2023-01-01', '负责人'),
(1, 4, '2023-02-01', '研究生'),
(2, 3, '2023-03-01', '负责人'),
(2, 5, '2023-04-01', '研究生');

-- ============================================
-- 4. 验证数据
-- ============================================

SELECT '数据库初始化完成！' as '';

SELECT '用户数量：' as '', COUNT(*) FROM users;
SELECT '项目数量：' as '', COUNT(*) FROM projects;
SELECT '论文数量：' as '', COUNT(*) FROM papers;
SELECT '团队数量：' as '', COUNT(*) FROM teams;

