# 登录注册系统

## 项目介绍
基于Spring MVC的用户登录注册系统，实现用户注册、登录、退出功能。

## 技术栈
Java 8、Spring MVC、Spring JDBC、MySQL、JSP、Maven

## 功能说明
- 首页：提供登录和注册入口
- 注册：用户名唯一性验证，注册成功跳转登录页
- 登录：验证用户名密码，登录成功进入主页面
- 主页面：显示用户信息，提供退出功能

## 快速启动

### 第一步：配置数据库
修改 applicationContext.xml 中的数据库用户名和密码

### 第二步：先测试再启动
双击 `run-test.bat` 测试配置是否正确

### 第三步：启动服务器
双击 start-server.bat 启动服务器

### 第四步：打开浏览器
等待服务器启动成功后，双击 open-browser.bat 自动打开浏览器

### 第四步：访问系统
浏览器自动打开 http://localhost:8080/login-system/

## 测试账号
用户名：admin
密码：123456

## 启动脚本说明
- start-server.bat：编译并启动Jetty服务器
- open-browser.bat：自动打开浏览器访问系统

## 常见问题
- 端口被占用：修改pom.xml中的端口号，或关闭占用8080端口的程序
- 数据库连接失败：检查MySQL服务是否启动，确认用户名密码正确
- 编译失败：执行 mvn clean compile 重新编译