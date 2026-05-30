@echo off
echo 正在打开浏览器...
ping 127.0.0.1 -n 3 >nul
start http://localhost:8080/login-system/