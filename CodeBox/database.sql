-- =============================================
-- CodeBox 代码片段管理器 - 数据库建表脚本
-- 包含：删除已存在表 + 创建表 + 插入数据（30+条）
-- =============================================

-- 1. 使用数据库
USE codebox;

-- 2. 删除已存在的表（先删子表，再删主表，避免外键约束报错）
DROP TABLE IF EXISTS `code_snippet`;
DROP TABLE IF EXISTS `user`;

-- 3. 创建用户表
CREATE TABLE `user` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. 创建代码片段表
CREATE TABLE `code_snippet` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `language` VARCHAR(30) NOT NULL,
  `tags` VARCHAR(200),
  `use_count` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 5. 插入测试用户
INSERT INTO `user` (`username`, `password`, `email`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@codebox.com'),
('test', 'e10adc3949ba59abbe56e057f20f883e', 'test@codebox.com');

-- =============================================
-- 6. Java 相关 (10条)
-- =============================================
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 8 Stream 过滤集合', 'List<User> adultUsers = users.stream().filter(user -> user.getAge() >= 18).collect(Collectors.toList());', 'Java', 'Stream,集合,Java8');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 读取文件内容', 'try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) { String line; while ((line = br.readLine()) != null) { System.out.println(line); } } catch (IOException e) { e.printStackTrace(); }', 'Java', 'IO,文件操作');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Spring Boot 定时任务', '@Scheduled(cron = "0 0 9 * * MON")\npublic void scheduledTask() {\n    System.out.println("每周一上午9点执行");\n}', 'Java', 'Spring,定时任务');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 单例模式-双重检查锁', 'public class Singleton {\n    private static volatile Singleton instance;\n    private Singleton() {}\n    public static Singleton getInstance() {\n        if (instance == null) {\n            synchronized (Singleton.class) {\n                if (instance == null) {\n                    instance = new Singleton();\n                }\n            }\n        }\n        return instance;\n    }\n}', 'Java', '设计模式,单例');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 多线程-线程池创建', 'ExecutorService executor = Executors.newFixedThreadPool(10);\nexecutor.submit(() -> {\n    System.out.println("任务执行");\n});\nexecutor.shutdown();', 'Java', '多线程,线程池');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 日期格式化', 'LocalDateTime now = LocalDateTime.now();\nDateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");\nString formatted = now.format(formatter);', 'Java', '日期,工具类');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'MyBatis Plus 分页查询', 'Page<User> page = new Page<>(pageNum, pageSize);\nLambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();\nwrapper.eq(User::getStatus, 1);\nPage<User> result = userMapper.selectPage(page, wrapper);', 'Java', 'MyBatisPlus,分页');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Spring Security 密码加密', '@Bean\npublic PasswordEncoder passwordEncoder() {\n    return new BCryptPasswordEncoder();\n}\nString encodedPassword = passwordEncoder().encode("123456");', 'Java', 'Spring,安全');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 冒泡排序算法', 'public void bubbleSort(int[] arr) {\n    for (int i = 0; i < arr.length - 1; i++) {\n        for (int j = 0; j < arr.length - 1 - i; j++) {\n            if (arr[j] > arr[j + 1]) {\n                int temp = arr[j];\n                arr[j] = arr[j + 1];\n                arr[j + 1] = temp;\n            }\n        }\n    }\n}', 'Java', '算法,排序');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Java 异常处理最佳实践', 'try {\n    // 业务代码\n} catch (SpecificException e) {\n    log.error("业务异常", e);\n    throw new BusinessException(e.getMessage());\n} catch (Exception e) {\n    log.error("系统异常", e);\n    throw new SystemException("系统繁忙");\n}', 'Java', '异常处理,最佳实践');

-- =============================================
-- 7. JavaScript 相关 (6条)
-- =============================================
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'JavaScript 异步/等待', 'async function fetchData() {\n    try {\n        const response = await fetch("/api/users");\n        const data = await response.json();\n        console.log(data);\n    } catch (error) {\n        console.error("请求失败:", error);\n    }\n}', 'JavaScript', '异步,API');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'React 组件示例', 'function Welcome(props) {\n    return <h1>Hello, {props.name}!</h1>;\n}\nfunction App() {\n    return (\n        <div>\n            <Welcome name="Alice" />\n            <Welcome name="Bob" />\n        </div>\n    );\n}', 'JavaScript', 'React,组件');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Vue 3 组合式 API', 'import { ref, onMounted } from "vue";\nexport default {\n    setup() {\n        const count = ref(0);\n        const increment = () => count.value++;\n        onMounted(() => {\n            console.log("组件已挂载");\n        });\n        return { count, increment };\n    }\n}', 'JavaScript', 'Vue,组合式API');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'JavaScript 防抖函数', 'function debounce(func, delay) {\n    let timer;\n    return function(...args) {\n        clearTimeout(timer);\n        timer = setTimeout(() => func.apply(this, args), delay);\n    };\n}\nconst debouncedSearch = debounce(searchInput, 300);', 'JavaScript', '工具函数,防抖');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'JavaScript 深拷贝', 'function deepClone(obj) {\n    if (obj === null || typeof obj !== "object") return obj;\n    if (obj instanceof Date) return new Date(obj);\n    if (obj instanceof Array) return obj.map(item => deepClone(item));\n    const clonedObj = {};\n    for (let key in obj) {\n        if (obj.hasOwnProperty(key)) {\n            clonedObj[key] = deepClone(obj[key]);\n        }\n    }\n    return clonedObj;\n}', 'JavaScript', '工具函数,深拷贝');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'JavaScript 数组排序', 'const numbers = [3, 1, 4, 1, 5, 9];\nnumbers.sort((a, b) => a - b);\nconst users = [{name: "张三", age: 25}, {name: "李四", age: 20}];\nusers.sort((a, b) => a.age - b.age);', 'JavaScript', '数组,排序');

-- =============================================
-- 8. Python 相关 (6条)
-- =============================================
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python 列表推导式', 'squares = [x**2 for x in range(10)]\nevens = [x for x in range(20) if x % 2 == 0]\npairs = [(x, y) for x in [1,2,3] for y in [3,1,4] if x != y]', 'Python', '列表推导式,Python技巧');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python 装饰器', 'def timer(func):\n    def wrapper(*args, **kwargs):\n        start = time.time()\n        result = func(*args, **kwargs)\n        end = time.time()\n        print(f"{func.__name__} took {end-start:.2f}s")\n        return result\n    return wrapper\n\n@timer\ndef slow_function():\n    time.sleep(1)', 'Python', '装饰器,高级特性');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python 快速排序', 'def quick_sort(arr):\n    if len(arr) <= 1:\n        return arr\n    pivot = arr[len(arr) // 2]\n    left = [x for x in arr if x < pivot]\n    middle = [x for x in arr if x == pivot]\n    right = [x for x in arr if x > pivot]\n    return quick_sort(left) + middle + quick_sort(right)', 'Python', '算法,排序');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python 爬虫示例', 'import requests\nfrom bs4 import BeautifulSoup\nurl = "https://example.com"\nresponse = requests.get(url)\nsoup = BeautifulSoup(response.text, "html.parser")\ntitles = soup.find_all("h1")\nfor title in titles:\n    print(title.text)', 'Python', '爬虫,网络请求');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python 数据处理-pandas', 'import pandas as pd\ndf = pd.read_csv("data.csv")\ndf.dropna(inplace=True)\ndf["date"] = pd.to_datetime(df["date"])\nresult = df.groupby("category")["value"].sum()', 'Python', '数据分析,pandas');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Python Flask 路由', 'from flask import Flask, jsonify, request\napp = Flask(__name__)\n\n@app.route("/api/users", methods=["GET"])\ndef get_users():\n    users = [{"id": 1, "name": "Alice"}]\n    return jsonify(users)\n\nif __name__ == "__main__":\n    app.run(debug=True)', 'Python', 'Flask,Web框架');

-- =============================================
-- 9. SQL 相关 (4条)
-- =============================================
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'SQL 复杂联表查询', 'SELECT u.username, u.email, COUNT(s.id) as snippet_count, SUM(s.use_count) as total_uses\nFROM user u\nLEFT JOIN code_snippet s ON u.id = s.user_id\nWHERE u.create_time >= "2024-01-01"\nGROUP BY u.id\nORDER BY total_uses DESC\nLIMIT 10;', 'SQL', '联表,分组,聚合');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'SQL 窗口函数-RANK', 'SELECT name, department, salary, RANK() OVER (PARTITION BY department ORDER BY salary DESC) as rank_in_dept FROM employees;', 'SQL', '窗口函数,排名');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'SQL 递归查询-CTE', 'WITH RECURSIVE cte AS (\n    SELECT id, name, parent_id, 1 as level\n    FROM categories\n    WHERE parent_id IS NULL\n    UNION ALL\n    SELECT c.id, c.name, c.parent_id, cte.level + 1\n    FROM categories c\n    INNER JOIN cte ON c.parent_id = cte.id\n)\nSELECT * FROM cte;', 'SQL', '递归,CTE');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'SQL 索引优化', 'CREATE INDEX idx_user_status_time ON user(status, create_time);\n-- 错误写法\nSELECT * FROM user WHERE DATE(create_time) = "2024-01-01";\n-- 正确写法\nSELECT * FROM user WHERE create_time >= "2024-01-01" AND create_time < "2024-01-02";', 'SQL', '索引,优化');


-- =============================================
-- HTML/CSS 相关代码 (4条)
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Flexbox 居中布局', 
'.container {\n    display: flex;\n    justify-content: center;\n    align-items: center;\n    height: 100vh;\n}', 
'HTML/CSS', 'Flexbox,布局,居中'),

(1, 'CSS 网格布局', 
'.grid-container {\n    display: grid;\n    grid-template-columns: repeat(3, 1fr);\n    gap: 20px;\n}\n.grid-item {\n    background: #f0f0f0;\n    padding: 20px;\n    border-radius: 8px;\n}', 
'HTML/CSS', 'Grid,网格布局'),

(1, '响应式导航栏', 
'<nav class="navbar">\n    <div class="logo">Logo</div>\n    <ul class="nav-menu">\n        <li><a href="#home">首页</a></li>\n        <li><a href="#about">关于</a></li>\n        <li><a href="#contact">联系</a></li>\n    </ul>\n</nav>\n\n<style>\n@media (max-width: 768px) {\n    .nav-menu {\n        display: none;\n    }\n}\n</style>', 
'HTML/CSS', '响应式,导航栏'),

(1, 'CSS 动画示例', 
'@keyframes fadeIn {\n    from { opacity: 0; transform: translateY(20px); }\n    to { opacity: 1; transform: translateY(0); }\n}\n\n.animate {\n    animation: fadeIn 0.5s ease-in-out;\n}', 
'HTML/CSS', '动画,CSS3'); 


-- XML 相关代码 (4条)
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Spring Bean 配置', 
'<?xml version="1.0" encoding="UTF-8"?>\n<beans xmlns="http://www.springframework.org/schema/beans"\n       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n       xsi:schemaLocation="http://www.springframework.org/schema/beans\n       http://www.springframework.org/schema/beans/spring-beans.xsd">\n\n    <bean id="userService" class="com.codebox.service.UserServiceImpl">\n        <property name="userMapper" ref="userMapper"/>\n    </bean>\n\n</beans>', 
'XML', 'Spring,配置'),

(1, 'Maven pom.xml 基础配置', 
'<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0"\n         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0\n         http://maven.apache.org/xsd/maven-4.0.0.xsd">\n    <modelVersion>4.0.0</modelVersion>\n\n    <groupId>com.example</groupId>\n    <artifactId>my-app</artifactId>\n    <version>1.0-SNAPSHOT</version>\n    <packaging>jar</packaging>\n\n</project>', 
'XML', 'Maven,配置'),

(1, 'MyBatis 映射文件', 
'<?xml version="1.0" encoding="UTF-8"?>\n<!DOCTYPE mapper\n        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"\n        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">\n<mapper namespace="com.example.mapper.UserMapper">\n\n    <select id="findById" resultType="User">\n        SELECT id, username, email\n        FROM user\n        WHERE id = #{id}\n    </select>\n\n</mapper>', 
'XML', 'MyBatis,映射文件'),

(1, 'Web.xml 配置', 
'<?xml version="1.0" encoding="UTF-8"?>\n<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"\n         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee\n         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"\n         version="4.0">\n\n    <servlet>\n        <servlet-name>dispatcher</servlet-name>\n        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>\n    </servlet>\n\n    <servlet-mapping>\n        <servlet-name>dispatcher</servlet-name>\n        <url-pattern>/</url-pattern>\n    </servlet-mapping>\n\n</web-app>', 
'XML', 'Web配置,Servlet');


-- =============================================
-- 10. 其他语言 (4条)
-- =============================================
INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Go 语言并发示例', 'package main\nimport (\n    "fmt"\n    "time"\n)\nfunc worker(id int, jobs <-chan int, results chan<- int) {\n    for job := range jobs {\n        fmt.Printf("Worker %d processing job %d\n", id, job)\n        time.Sleep(time.Second)\n        results <- job * 2\n    }\n}\nfunc main() {\n    jobs := make(chan int, 100)\n    results := make(chan int, 100)\n    for w := 1; w <= 3; w++ {\n        go worker(w, jobs, results)\n    }\n    for j := 1; j <= 5; j++ {\n        jobs <- j\n    }\n    close(jobs)\n}', 'Go', '并发,Channel');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'TypeScript 类型定义', 'interface User {\n    id: number;\n    name: string;\n    email?: string;\n    readonly createdAt: Date;\n}\ntype ApiResponse<T> = {\n    code: number;\n    message: string;\n    data: T;\n};\nfunction fetchUser(id: number): Promise<ApiResponse<User>> {\n    return fetch(`/api/users/${id}`).then(res => res.json());\n}', 'TypeScript', '类型定义,接口');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Docker 基础命令', 'docker build -t myapp:latest .\ndocker run -d -p 8080:8080 --name myapp myapp:latest\ndocker logs -f myapp\ndocker exec -it myapp /bin/bash\ndocker stop myapp\ndocker rm myapp', 'Docker', '容器,DevOps');

INSERT INTO `code_snippet` (`user_id`, `title`, `content`, `language`, `tags`) VALUES
(1, 'Git 常用命令', 'git checkout -b feature/new-feature\ngit branch -d old-branch\ngit log --oneline --graph\ngit commit --amend -m "修正提交信息"\ngit reset --soft HEAD~1\ngit reset --hard HEAD~1\ngit stash save "临时保存"\ngit stash pop\ngit merge feature --no-ff', 'Git', '版本控制,命令');

-- =============================================
-- 11. 验证结果
-- =============================================
SELECT COUNT(*) AS total_users FROM user;
SELECT COUNT(*) AS total_snippets FROM code_snippet;
SELECT language, COUNT(*) AS count FROM code_snippet GROUP BY language;