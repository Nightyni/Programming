<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${snippet.title} - CodeBox</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/lib/bootstrap/css/bootstrap.min.css">
    <!-- 代码高亮 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/atom-one-dark.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
    <script>hljs.highlightAll();</script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            background: #f5f7fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            box-shadow: 0 2px 20px rgba(0,0,0,0.1);
            padding: 15px 0;
        }
        .navbar-brand {
            font-size: 24px;
            font-weight: bold;
            color: white !important;
        }
        .navbar-brand i {
            margin-right: 10px;
        }
        .user-info {
            color: white;
            display: flex;
            align-items: center;
            gap: 20px;
        }
        .logout-btn {
            background: rgba(255,255,255,0.2);
            padding: 8px 20px;
            border-radius: 20px;
            color: white;
            text-decoration: none;
            transition: all 0.3s;
        }
        .logout-btn:hover {
            background: rgba(255,255,255,0.3);
            color: white;
        }
        .detail-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            overflow: hidden;
            margin-top: 40px;
        }
        .detail-header {
            padding: 25px 30px;
            border-bottom: 1px solid #f0f0f0;
            background: #fafbfc;
        }
        .detail-header h2 {
            margin: 0 0 10px 0;
            color: #333;
        }
        .detail-meta {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            align-items: center;
        }
        .language-badge {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: bold;
        }
        .tag-badge {
            background: #e8eaf6;
            color: #5c6bc0;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
        }
        .use-count {
            color: #999;
            font-size: 13px;
        }
        .detail-body {
            padding: 30px;
        }
        .code-container {
            background: #1e1e1e;
            border-radius: 12px;
            overflow: hidden;
            margin-bottom: 20px;
        }
        .code-header {
            background: #2d2d2d;
            padding: 10px 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #3d3d3d;
        }
        .code-language {
            color: #ddd;
            font-size: 12px;
        }
        .copy-btn {
            background: #3d3d3d;
            border: none;
            color: #ddd;
            padding: 5px 15px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 12px;
            transition: all 0.2s;
        }
        .copy-btn:hover {
            background: #5c6bc0;
            color: white;
        }
        pre {
            margin: 0;
            padding: 20px;
            background: #1e1e1e;
            overflow-x: auto;
        }
        code {
            font-family: 'Fira Code', 'Consolas', monospace;
            font-size: 13px;
            line-height: 1.5;
        }
        .detail-footer {
            padding: 20px 30px;
            background: #f8f9fa;
            border-top: 1px solid #f0f0f0;
        }
        .btn-back {
            background: #6c757d;
            border: none;
            border-radius: 10px;
            padding: 10px 25px;
            color: white;
            text-decoration: none;
            transition: all 0.2s;
        }
        .btn-back:hover {
            background: #5a6268;
            color: white;
        }
        .btn-edit {
            background: #f39c12;
            border: none;
            border-radius: 10px;
            padding: 10px 25px;
            color: white;
            text-decoration: none;
            margin-left: 10px;
            transition: all 0.2s;
        }
        .btn-edit:hover {
            background: #e67e22;
            color: white;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/snippet/list">
                <i class="fas fa-code"></i> CodeBox
            </a>
            <div class="user-info">
                <span><i class="fas fa-user-circle"></i> ${sessionScope.user.username}</span>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
                    <i class="fas fa-sign-out-alt"></i> 退出
                </a>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="detail-card">
            <div class="detail-header">
                <h2>${snippet.title}</h2>
                <div class="detail-meta">
                    <span class="language-badge">${snippet.language}</span>
                    <c:if test="${not empty snippet.tags}">
                        <c:forEach var="tag" items="${fn:split(snippet.tags, ',')}">
                            <span class="tag-badge"><i class="fas fa-tag"></i> ${fn:trim(tag)}</span>
                        </c:forEach>
                    </c:if>
                    <span class="use-count"><i class="fas fa-eye"></i> 使用次数：${snippet.useCount}</span>
                    <span class="use-count"><i class="far fa-calendar-alt"></i> 创建时间：${snippet.createTime}</span>
                </div>
            </div>
            <div class="detail-body">
                <div class="code-container">
                    <div class="code-header">
                        <span class="code-language">${snippet.language}</span>
                        <button class="copy-btn" onclick="copyCode()">
                            <i class="fas fa-copy"></i> 一键复制
                        </button>
                    </div>
                    <pre><code id="codeContent" class="language-${fn:toLowerCase(snippet.language)}">${snippet.content}</code></pre>
                </div>
            </div>
            <div class="detail-footer">
                <a href="${pageContext.request.contextPath}/snippet/list" class="btn-back">
                    <i class="fas fa-arrow-left"></i> 返回列表
                </a>
                <a href="${pageContext.request.contextPath}/snippet/edit?id=${snippet.id}" class="btn-edit">
                    <i class="fas fa-edit"></i> 编辑
                </a>
            </div>
        </div>
    </div>

    <script src="https://kit.fontawesome.com/a81368914c.js"></script>
    <script>
        function copyCode() {
            const code = document.getElementById('codeContent').innerText;
            navigator.clipboard.writeText(code).then(() => {
                alert('代码已复制到剪贴板！');
            });
        }
    </script>
</body>
</html>