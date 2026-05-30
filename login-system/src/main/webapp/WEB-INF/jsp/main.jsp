<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>主页面</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0;
            padding: 0;
            min-height: 100vh;
        }
        
        .navbar {
            background: rgba(255,255,255,0.95);
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .welcome {
            font-size: 18px;
            color: #333;
        }
        
        .welcome strong {
            color: #667eea;
        }
        
        .logout-btn {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 8px 20px;
            text-decoration: none;
            border-radius: 5px;
            transition: transform 0.3s;
            display: inline-block;
        }
        
        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        
        .content {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: calc(100vh - 70px);
            padding: 20px;
        }
        
        .card {
            background: white;
            padding: 50px;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            text-align: center;
            animation: fadeIn 0.5s ease;
            max-width: 500px;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: scale(0.9);
            }
            to {
                opacity: 1;
                transform: scale(1);
            }
        }
        
        .card h2 {
            color: #667eea;
            margin-bottom: 20px;
            font-size: 32px;
        }
        
        .user-info {
            background: #f5f5f5;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
            text-align: left;
        }
        
        .user-info p {
            margin: 10px 0;
            font-size: 16px;
        }
        
        .user-info strong {
            color: #667eea;
        }
        
        .success-icon {
            font-size: 60px;
            color: #4caf50;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="welcome">
            欢迎，<strong>${sessionScope.loginUser.username}</strong>
        </div>
        <a href="${pageContext.request.contextPath}/user/logout" class="logout-btn">退出登录</a>
    </div>
    
    <div class="content">
        <div class="card">
            <div class="success-icon">✓</div>
            <h2>登录成功！</h2>
            <div class="user-info">
                <p><strong>用户名：</strong> ${sessionScope.loginUser.username}</p>
                <p><strong>邮箱：</strong> ${sessionScope.loginUser.email}</p>
                <p><strong>登录时间：</strong> <%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) %></p>
            </div>
            <p style="color: #666;">欢迎来到用户系统，您已成功登录！</p>
        </div>
    </div>
</body>
</html>