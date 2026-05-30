@echo off
chcp 65001 >nul
title 个人任务管理系统 - 图形界面版

echo 编译项目...
if exist bin rmdir /s /q bin
mkdir bin

javac -Xlint:none -d bin -cp "lib/*" src/main/java/com/taskmanagement/model/*.java
javac -Xlint:none -d bin -cp "bin;lib/*" src/main/java/com/taskmanagement/dao/*.java
javac -Xlint:none -d bin -cp "bin;lib/*" src/main/java/com/taskmanagement/service/*.java
javac -Xlint:none -d bin -cp "bin;lib/*" src/main/java/com/taskmanagement/ui/*.java
javac -Xlint:none -d bin -cp "bin;lib/*" src/main/java/com/taskmanagement/Main.java

echo 启动系统...
java -cp "bin;lib/*" com.taskmanagement.Main

pause

