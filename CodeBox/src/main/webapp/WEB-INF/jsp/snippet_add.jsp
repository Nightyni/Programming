<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>新增代码片段 - CodeBox</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/atom-one-dark.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .navbar {
            background: rgba(255,255,255,0.1);
            backdrop-filter: blur(10px);
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
        .container {
            padding: 40px 0;
        }
        .form-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
            max-width: 1000px;
            margin: 0 auto;
            animation: fadeInUp 0.6s ease;
        }
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        .form-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .form-header h1 {
            font-size: 28px;
            margin-bottom: 10px;
        }
        .form-header p {
            opacity: 0.9;
            font-size: 14px;
        }
        .form-body {
            padding: 35px;
        }
        .form-group {
            margin-bottom: 25px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
            font-size: 14px;
        }
        .form-group label i {
            margin-right: 8px;
            color: #667eea;
        }
        .form-control {
            width: 100%;
            padding: 10px 15px;
            font-size: 14px;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            transition: all 0.3s;
            background-color: white;
            box-sizing: border-box;
        }
        input.form-control {
            height: 44px;
            line-height: 22px;
        }
        select.form-control {
            height: 44px;
            line-height: 22px;
            cursor: pointer;
            appearance: none;
            -webkit-appearance: none;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right 15px center;
            background-size: 16px;
            padding-right: 40px;
        }
        /* 代码编辑器容器 */
        .code-editor-container {
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            overflow: hidden;
            transition: all 0.3s;
        }
        .code-editor-container:focus-within {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102,126,234,0.1);
        }
        .code-editor-header {
            background: #2d2d2d;
            padding: 10px 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #3d3d3d;
        }
        .code-editor-header span {
            color: #ddd;
            font-size: 12px;
            font-family: monospace;
        }
        .code-editor-header i {
            color: #667eea;
        }
        /* 代码输入框 - 固定高度 + 滚动 */
        .code-textarea {
            width: 100%;
            height: 400px;
            padding: 15px;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.5;
            border: none;
            resize: none;
            background: #1e1e1e;
            color: #d4d4d4;
            outline: none;
            overflow: auto;
        }
        .form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102,126,234,0.1);
        }
        .alert {
            padding: 12px 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .alert-danger {
            background: #fee;
            color: #c33;
            border-left: 4px solid #c33;
        }
        .form-actions {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        .btn-submit {
            flex: 1;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            color: white;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
            height: 48px;
        }
        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102,126,234,0.4);
        }
        .btn-cancel {
            flex: 1;
            background: #6c757d;
            border: none;
            border-radius: 10px;
            padding: 12px;
            color: white;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            transition: all 0.3s;
            height: 48px;
            line-height: 24px;
            display: inline-block;
        }
        .btn-cancel:hover {
            background: #5a6268;
            color: white;
            text-decoration: none;
            transform: translateY(-2px);
        }
        .help-text {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
        }
        .required {
            color: #e74c3c;
            margin-left: 4px;
        }
        /* 预览区域 - 同样固定高度 + 滚动 */
        .preview-section {
            margin-top: 20px;
            border-top: 1px solid #f0f0f0;
            padding-top: 20px;
        }
        .preview-title {
            font-size: 14px;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
        }
        .preview-title i {
            color: #667eea;
            margin-right: 8px;
        }
        .preview-content {
            background: #1e1e1e;
            border-radius: 10px;
            overflow: hidden;
            height: 400px;
            display: flex;
            flex-direction: column;
        }
        .preview-header {
            background: #2d2d2d;
            padding: 8px 15px;
            color: #ddd;
            font-size: 12px;
            border-bottom: 1px solid #3d3d3d;
            flex-shrink: 0;
        }
        .preview-body {
            flex: 1;
            overflow: auto;
            padding: 15px;
        }
        pre {
            margin: 0;
            background: #1e1e1e;
            overflow: visible;
        }
        code {
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 12px;
            line-height: 1.5;
            white-space: pre-wrap;
            word-break: break-all;
        }
        .hljs {
            background: transparent;
            padding: 0;
        }
    </style>
</head>
<body>
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
        <div class="form-card">
            <div class="form-header">
                <h1><i class="fas fa-plus-circle"></i> 新增代码片段</h1>
                <p>收藏你常用的代码，方便随时查阅</p>
            </div>
            <div class="form-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/snippet/add" method="post" id="addForm">
                    <div class="form-group">
                        <label><i class="fas fa-heading"></i> 标题 <span class="required">*</span></label>
                        <input type="text" name="title" class="form-control" placeholder="例如：MyBatis 分页查询工具类" required value="${snippet.title}">
                        <div class="help-text">简洁明了地描述这段代码的作用</div>
                    </div>
                    
                    <div class="form-group">
                        <label><i class="fas fa-code"></i> 编程语言 <span class="required">*</span></label>
                        <select name="language" id="languageSelect" class="form-control" required>
                            <option value="">请选择编程语言</option>
                            <c:forEach var="lang" items="${languageList}">
                                <option value="${lang}" ${snippet.language == lang ? 'selected' : ''}>${lang}</option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label><i class="fas fa-tags"></i> 标签</label>
                        <input type="text" name="tags" class="form-control" placeholder="例如：工具类,分页,MyBatis（多个用英文逗号分隔）" value="${snippet.tags}">
                        <div class="help-text">添加标签方便搜索，多个标签用英文逗号分隔</div>
                    </div>
                    
                    <div class="form-group">
                        <label><i class="fas fa-file-code"></i> 代码内容 <span class="required">*</span></label>
                        <div class="code-editor-container">
                            <div class="code-editor-header">
                                <span><i class="fas fa-terminal"></i> 代码编辑器</span>
                                <span id="langHint" style="font-size: 11px;">未选择语言</span>
                            </div>
                            <textarea id="codeInput" name="content" class="code-textarea" placeholder="粘贴你的代码到这里..." oninput="updatePreview()" required>${snippet.content}</textarea>
                        </div>
                        <div class="help-text">支持 Java、JavaScript、Python、SQL 等多种语言，实时预览高亮效果</div>
                    </div>
                    
                    <!-- 实时预览区域 - 固定高度 + 滚动 -->
                    <div class="preview-section">
                        <div class="preview-title">
                            <i class="fas fa-eye"></i> 实时预览
                        </div>
                        <div class="preview-content">
                            <div class="preview-header">
                                <i class="fas fa-code"></i> 高亮预览
                            </div>
                            <div class="preview-body">
                                <pre><code id="previewCode" class="hljs"></code></pre>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-submit">
                            <i class="fas fa-save"></i> 保存代码
                        </button>
                        <a href="${pageContext.request.contextPath}/snippet/list" class="btn-cancel">
                            <i class="fas fa-times"></i> 取消
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/lib/jquery/jquery-3.6.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
    <script>
        function getLangForHighlight(lang) {
            const langMap = {
                'JavaScript': 'javascript',
                'Java': 'java',
                'Python': 'python',
                'SQL': 'sql',
                'HTML/CSS': 'html',
                'XML': 'xml',
                'Go': 'go',
                'TypeScript': 'typescript',
                'Docker': 'dockerfile',
                'Git': 'bash',
                'Other': 'plaintext'
            };
            return langMap[lang] || 'plaintext';
        }
        
        function updatePreview() {
            const code = document.getElementById('codeInput').value;
            const language = document.getElementById('languageSelect').value;
            const langForHighlight = getLangForHighlight(language);
            
            const previewCode = document.getElementById('previewCode');
            previewCode.className = `hljs language-${langForHighlight}`;
            
            if (code.trim()) {
                try {
                    const highlighted = hljs.highlight(code, { language: langForHighlight }).value;
                    previewCode.innerHTML = highlighted;
                } catch (e) {
                    previewCode.innerHTML = escapeHtml(code);
                }
            } else {
                previewCode.innerHTML = '<span style="color: #666;">// 在这里输入代码，实时预览高亮效果...</span>';
            }
            
            const langHint = document.getElementById('langHint');
            if (language) {
                langHint.innerHTML = language;
                langHint.style.color = '#667eea';
            } else {
                langHint.innerHTML = '未选择语言';
                langHint.style.color = '#888';
            }
        }
        
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            updatePreview();
            document.getElementById('languageSelect').addEventListener('change', updatePreview);
        });
    </script>
</body>
</html>