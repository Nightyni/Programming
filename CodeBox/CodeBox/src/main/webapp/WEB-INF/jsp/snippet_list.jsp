<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>CodeBox - 我的代码库</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
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
        /* 导航栏 */
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
        .user-info span {
            opacity: 0.9;
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
        /* 搜索卡片 */
        .search-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
        /* 搜索表单 - 使用 flex 换行 */
        .search-form {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            align-items: flex-end;
        }
        .search-group {
            flex: 2;
            min-width: 180px;
        }
        .search-group-small {
            flex: 1;
            min-width: 180px;
        }
        .search-group-tiny {
            width: 100px;
        }
        .search-label {
            display: block;
            margin-bottom: 8px;
            font-size: 13px;
            color: #666;
            font-weight: 500;
        }
        /* 表单控件通用样式 */
        .form-control {
            width: 100%;
            border-radius: 10px;
            border: 1px solid #e0e0e0;
            padding: 8px 12px;
            font-size: 14px;
            transition: all 0.3s;
            background-color: white;
            box-sizing: border-box;
        }
        .form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102,126,234,0.1);
        }
        /* input 输入框 */
        input.form-control {
            height: 38px;
            line-height: 1.4;
        }
        /* select 下拉框 - 修复垂直居中 */
        select.form-control {
            height: 38px;
            line-height: normal;
            cursor: pointer;
            appearance: none;
            -webkit-appearance: none;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right 12px center;
            background-size: 16px;
            padding-right: 36px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 10px 20px;
            color: white;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102,126,234,0.4);
        }
        .btn-success {
            background: #27ae60;
            border: none;
            border-radius: 10px;
            padding: 10px 20px;
            margin-left: 10px;
            color: white;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            transition: all 0.3s;
        }
        .btn-success:hover {
            background: #219a52;
            text-decoration: none;
            color: white;
            transform: translateY(-2px);
        }
        .search-actions {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        /* 卡片网格 - 固定每行2个 */
        .card-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 25px;
            margin-bottom: 30px;
        }
        @media (max-width: 768px) {
            .card-grid {
                grid-template-columns: 1fr;
            }
        }
        .code-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            transition: all 0.3s;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        .code-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        }
        .card-header {
            padding: 18px 20px;
            border-bottom: 1px solid #f0f0f0;
            position: relative;
            background: #fafbfc;
        }
        .card-header h4 {
            font-size: 17px;
            margin: 0;
            color: #333;
            padding-right: 70px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .language-badge {
            position: absolute;
            top: 15px;
            right: 15px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: bold;
        }
        .card-body {
            padding: 18px 20px;
            flex: 1;
        }
        .code-preview {
            background: #1e1e1e;
            color: #d4d4d4;
            padding: 12px;
            border-radius: 10px;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 11px;
            line-height: 1.5;
            max-height: 130px;
            overflow: hidden;
            margin-bottom: 15px;
            white-space: pre-wrap;
            word-break: break-all;
        }
        .tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 10px;
        }
        .tag {
            background: #e8eaf6;
            color: #5c6bc0;
            padding: 3px 10px;
            border-radius: 15px;
            font-size: 11px;
        }
        .card-footer {
            padding: 12px 20px;
            background: #f8f9fa;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 11px;
            color: #999;
            border-top: 1px solid #f0f0f0;
        }
        .card-actions {
            display: flex;
            gap: 8px;
        }
        .btn-icon {
            padding: 5px 12px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 12px;
            transition: all 0.2s;
        }
        .btn-view { background: #e8eaf6; color: #5c6bc0; }
        .btn-view:hover { background: #5c6bc0; color: white; text-decoration: none; }
        .btn-edit { background: #fff3e0; color: #f39c12; }
        .btn-edit:hover { background: #f39c12; color: white; text-decoration: none; }
        .btn-delete { background: #fee; color: #e74c3c; }
        .btn-delete:hover { background: #e74c3c; color: white; text-decoration: none; }
        /* 分页 */
        .pagination {
            justify-content: center;
            margin-top: 20px;
        }
        .page-link {
            color: #667eea;
            border-radius: 8px;
            margin: 0 3px;
        }
        .page-item.active .page-link {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: transparent;
            color: white;
        }
        .empty-state {
            text-align: center;
            padding: 60px;
            background: white;
            border-radius: 15px;
        }
        .empty-state i {
            font-size: 64px;
            color: #ddd;
            margin-bottom: 20px;
        }
        .stats-info {
            text-align: center;
            margin-top: 20px;
            color: #999;
            font-size: 13px;
        }
        .code-card * {
            max-width: 100%;
            word-wrap: break-word;
        }
        .code-preview code {
            background: transparent;
            color: #d4d4d4;
            font-family: inherit;
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

    <div class="container" style="padding: 40px 0;">
        <!-- 搜索栏 -->
        <div class="search-card">
            <form action="${pageContext.request.contextPath}/snippet/list" method="get" class="search-form">
                <div class="search-group">
                    <label class="search-label">🔍 标题 / 内容</label>
                    <input type="text" name="keyword" class="form-control" placeholder="输入关键词搜索..." value="${keyword}">
                </div>
                <div class="search-group-small">
                    <label class="search-label">💻 编程语言</label>
                    <select name="language" class="form-control">
                        <option value="">全部语言</option>
                        <c:forEach var="lang" items="${languageList}">
                            <option value="${lang}" ${language == lang ? 'selected' : ''}>${lang}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="search-group-small">
                    <label class="search-label">🏷️ 标签</label>
                    <input type="text" name="tags" class="form-control" placeholder="如：工具类,算法" value="${tags}">
                </div>
                <div class="search-actions">
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-search"></i> 搜索
                    </button>
                    <a href="${pageContext.request.contextPath}/snippet/add" class="btn-success">
                        <i class="fas fa-plus"></i> 新增代码
                    </a>
                </div>
            </form>
        </div>

        <!-- 代码卡片网格 -->
        <c:choose>
            <c:when test="${empty page.list}">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h4>暂无代码片段</h4>
                    <p>点击「新增代码」开始收藏你的第一段代码吧</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card-grid">
                    <c:forEach var="snippet" items="${page.list}">
                        <div class="code-card">
                            <div class="card-header">
                                <h4 title="${snippet.title}">${fn:substring(snippet.title, 0, 35)}${fn:length(snippet.title) > 35 ? '...' : ''}</h4>
                                <span class="language-badge">${snippet.language}</span>
                            </div>
                            <div class="card-body">
                                <div class="code-preview">
                                    <code>${fn:escapeXml(fn:substring(snippet.content, 0, 150))}${fn:length(snippet.content) > 150 ? '...' : ''}</code>
                                </div>
                                <c:if test="${not empty snippet.tags}">
                                    <div class="tags">
                                        <c:forEach var="tag" items="${fn:split(snippet.tags, ',')}">
                                            <span class="tag"><i class="fas fa-tag"></i> ${fn:trim(tag)}</span>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                            <div class="card-footer">
                                <div>
                                    <i class="far fa-calendar-alt"></i> ${fn:substring(snippet.createTime, 0, 16)} &nbsp;|&nbsp;
                                    <i class="fas fa-eye"></i> ${snippet.useCount} 次使用
                                </div>
                                <div class="card-actions">
                                    <a href="${pageContext.request.contextPath}/snippet/detail?id=${snippet.id}" class="btn-icon btn-view"><i class="fas fa-eye"></i> 查看</a>
                                    <a href="${pageContext.request.contextPath}/snippet/edit?id=${snippet.id}" class="btn-icon btn-edit"><i class="fas fa-edit"></i> 编辑</a>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${snippet.id})" class="btn-icon btn-delete"><i class="fas fa-trash"></i> 删除</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- 分页 -->
        <c:if test="${page.totalPages > 1}">
            <ul class="pagination">
                <li class="page-item ${!page.hasPrev ? 'disabled' : ''}">
                    <a class="page-link" href="?pageNum=${page.pageNum-1}&keyword=${keyword}&language=${language}&tags=${tags}">上一页</a>
                </li>
                <c:forEach begin="1" end="${page.totalPages}" var="i">
                    <li class="page-item ${page.pageNum == i ? 'active' : ''}">
                        <a class="page-link" href="?pageNum=${i}&keyword=${keyword}&language=${language}&tags=${tags}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item ${!page.hasNext ? 'disabled' : ''}">
                    <a class="page-link" href="?pageNum=${page.pageNum+1}&keyword=${keyword}&language=${language}&tags=${tags}">下一页</a>
                </li>
            </ul>
        </c:if>
        
        <!-- 统计信息 -->
        <div class="stats-info">
            共 ${page.totalCount} 条代码片段，第 ${page.pageNum} / ${page.totalPages} 页，每页 ${page.pageSize} 条
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/lib/jquery/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/bootstrap/js/bootstrap.min.js"></script>
    <script>
        function confirmDelete(id) {
            if (confirm('确定要删除这段代码吗？')) {
                window.location.href = '${pageContext.request.contextPath}/snippet/delete?id=' + id;
            }
        }
    </script>
</body>
</html>