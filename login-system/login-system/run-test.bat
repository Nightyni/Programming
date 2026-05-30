@echo off
chcp 65001 >nul
title 登录系统 - Spring配置测试

echo ========================================
echo      登录系统 - Spring配置测试
echo ========================================
echo.

echo [1/3] 编译项目...
call mvn clean compile test-compile
if %errorlevel% neq 0 (
    echo 编译失败
    pause
    exit /b 1
)
echo 编译成功
echo.

echo [2/3] 运行Spring测试...
echo 正在测试Spring容器和数据库连接...
echo.

java -cp "target\classes;target\test-classes;%USERPROFILE%\.m2\repository\org\springframework\spring-context\5.3.23\spring-context-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-beans\5.3.23\spring-beans-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-core\5.3.23\spring-core-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-jcl\5.3.23\spring-jcl-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-expression\5.3.23\spring-expression-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-aop\5.3.23\spring-aop-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-jdbc\5.3.23\spring-jdbc-5.3.23.jar;%USERPROFILE%\.m2\repository\org\springframework\spring-tx\5.3.23\spring-tx-5.3.23.jar;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%USERPROFILE%\.m2\repository\com\alibaba\druid\1.2.16\druid-1.2.16.jar;%USERPROFILE%\.m2\repository\javax\annotation\javax.annotation-api\1.3.2\javax.annotation-api-1.3.2.jar" com.test.SpringTest

echo.
echo [3/3] 测试完成
echo.
pause