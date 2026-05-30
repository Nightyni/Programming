package com.taskmanagement.ui;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("all")
public class ConsoleUI {
    private Scanner scanner;
    private UserService userService;
    private TaskService taskService;
    private User currentUser;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.taskService = new TaskService();
        this.currentUser = null;
    }
    
    /**
     * 显示主菜单
     */
    public void showMainMenu() {
        while (true) {
            System.out.println("\n" + "═".repeat(50));
            System.out.println("          个人任务管理系统");
            System.out.println("═".repeat(50));
            System.out.println("1. 用户注册");
            System.out.println("2. 用户登录");
            System.out.println("3. 管理员登录");
            System.out.println("4. 退出系统");
            System.out.print("请选择操作: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    loginUser();
                    break;
                case "3":
                    adminLogin();
                    break;
                case "4":
                    System.out.println("感谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选择，请重新输入");
            }
        }
    }
    
    /**
     * 用户注册
     */
    private void registerUser() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("                用户注册");
        System.out.println("─".repeat(40));
        
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        
        System.out.print("请输入密码(至少6位): ");
        String password = scanner.nextLine();
        
        System.out.print("请输入邮箱: ");
        String email = scanner.nextLine();
        
        if (userService.register(username, password, email)) {
            System.out.println("✅ 注册成功！");
        } else {
            System.out.println("❌ 注册失败，用户名可能已存在");
        }
    }
    
    /**
     * 用户登录
     */
    private void loginUser() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("                用户登录");
        System.out.println("─".repeat(40));
        
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        
        currentUser = userService.login(username, password);
        
        if (currentUser != null) {
            System.out.println("✅ 登录成功！欢迎 " + currentUser.getUsername());
            showUserMenu();
        } else {
            System.out.println("❌ 登录失败，用户名或密码错误");
        }
    }
    
    /**
     * 管理员登录
     */
    private void adminLogin() {
        System.out.println("\n管理员登录（使用默认账号 admin/admin123）");
        
        currentUser = userService.login("admin", "admin123");
        
        if (currentUser != null) {
            System.out.println("✅ 管理员登录成功！");
            showAdminMenu();
        } else {
            System.out.println("❌ 管理员登录失败");
        }
    }
    
    /**
     * 显示用户菜单
     */
    private void showUserMenu() {
        while (currentUser != null) {
            System.out.println("\n" + "═".repeat(50));
            System.out.println("          任务管理菜单 - 用户: " + currentUser.getUsername());
            System.out.println("═".repeat(50));
            System.out.println("1. 查看所有任务");
            System.out.println("2. 查看待完成任务");
            System.out.println("3. 查看今日到期任务");
            System.out.println("4. 添加新任务");
            System.out.println("5. 修改任务状态");
            System.out.println("6. 删除任务");
            System.out.println("7. 任务统计");
            System.out.println("8. 用户信息");
            System.out.println("9. 退出登录");
            System.out.println("0. 退出系统");
            System.out.print("请选择操作 (0-9): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewAllTasks();
                    break;
                case "2":
                    viewPendingTasks();
                    break;
                case "3":
                    viewTodayTasks();
                    break;
                case "4":
                    addNewTask();
                    break;
                case "5":
                    updateTaskStatus();
                    break;
                case "6":
                    deleteTask();
                    break;
                case "7":
                    showTaskStatistics();
                    break;
                case "8":
                    showUserInfo();
                    break;
                case "9":
                    System.out.println("已退出登录");
                    currentUser = null;
                    return;
                case "0":
                    System.out.println("感谢使用，再见！");
                    System.exit(0);
                default:
                    System.out.println("❌ 无效选择，请输入0-9的数字");
            }
        }
    }
    
    /**
     * 显示管理员菜单
     */
    private void showAdminMenu() {
        while (currentUser != null) {
            System.out.println("\n" + "═".repeat(50));
            System.out.println("          管理员菜单");
            System.out.println("═".repeat(50));
            System.out.println("1. 查看所有用户");
            System.out.println("2. 查看所有任务");
            System.out.println("3. 删除用户");
            System.out.println("4. 查看系统统计");
            System.out.println("5. 返回用户菜单");
            System.out.println("6. 退出登录");
            System.out.print("请选择操作 (1-6): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewAllTasksAdmin();
                    break;
                case "3":
                    deleteUserAdmin();
                    break;
                case "4":
                    showSystemStatistics();
                    break;
                case "5":
                    showUserMenu();
                    break;
                case "6":
                    System.out.println("已退出登录");
                    currentUser = null;
                    return;
                default:
                    System.out.println("无效选择");
            }
        }
    }
    
    /**
     * 查看所有任务
     */
    private void viewAllTasks() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("                    我的所有任务");
        System.out.println("─".repeat(60));
        
        List<Task> tasks = taskService.getUserTasks(currentUser.getUserId());
        
        if (tasks.isEmpty()) {
            System.out.println("暂无任务");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.println("【任务 " + (i+1) + "】");
                System.out.println("  ID: " + task.getTaskId());
                System.out.println("  标题: " + task.getTitle());
                System.out.println("  状态: " + getStatusIcon(task.getStatus()) + task.getStatus());
                System.out.println("  优先级: " + getPriorityIcon(task.getPriority()) + task.getPriority());
                System.out.println("  截止日期: " + task.getDueDate());
                System.out.println("  描述: " + (task.getDescription() != null ? task.getDescription() : "无"));
                System.out.println("─".repeat(40));
            }
        }
    }
    
    /**
     * 查看待完成任务
     */
    private void viewPendingTasks() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("                    待完成任务");
        System.out.println("─".repeat(60));
        
        List<Task> tasks = taskService.getTasksByStatus(currentUser.getUserId(), "待完成");
        
        if (tasks.isEmpty()) {
            System.out.println("暂无待完成任务");
        } else {
            for (Task task : tasks) {
                System.out.println("📋 " + task.getTitle() + " | 📅 " + task.getDueDate() + " | ⚡ " + task.getPriority());
            }
        }
    }
    
    /**
     * 查看今日任务
     */
    private void viewTodayTasks() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("                    今日到期任务");
        System.out.println("─".repeat(60));
        
        List<Task> tasks = taskService.getTodayTasks(currentUser.getUserId());
        
        if (tasks.isEmpty()) {
            System.out.println("今日无到期任务");
        } else {
            for (Task task : tasks) {
                System.out.println("🚨 " + task.getTitle() + " | " + task.getStatus() + " | " + task.getPriority());
            }
        }
    }
    
    /**
     * 添加新任务
     */
    private void addNewTask() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("                  添加新任务");
        System.out.println("─".repeat(40));
        
        System.out.print("任务标题: ");
        String title = scanner.nextLine();
        
        System.out.print("任务描述: ");
        String description = scanner.nextLine();
        
        System.out.print("优先级 (1-低 2-中 3-高): ");
        String priorityChoice = scanner.nextLine();
        
        // 修正的switch语句 - 传统语法
        String priority;
        switch (priorityChoice) {
            case "1":
                priority = "低";
                break;
            case "3":
                priority = "高";
                break;
            default:
                priority = "中";
                break;
        }
        
        LocalDate dueDate = null;
        while (dueDate == null) {
            System.out.print("截止日期 (格式: 2025-12-30): ");
            String dateStr = scanner.nextLine();
            
            try {
                dueDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                if (dueDate.isBefore(LocalDate.now())) {
                    System.out.println("❌ 截止日期不能早于今天");
                    dueDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("❌ 日期格式错误，请使用 yyyy-MM-dd 格式");
            }
        }
        
        if (taskService.createTask(currentUser.getUserId(), title, description, priority, dueDate)) {
            System.out.println("✅ 任务添加成功！");
        } else {
            System.out.println("❌ 任务添加失败");
        }
    }
    
    /**
     * 更新任务状态
     */
    private void updateTaskStatus() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("                  更新任务状态");
        System.out.println("─".repeat(40));
        
        viewAllTasks();
        
        System.out.print("请输入要更新的任务ID: ");
        try {
            int taskId = Integer.parseInt(scanner.nextLine());
            
            System.out.println("选择新状态:");
            System.out.println("1. 🔄 待完成");
            System.out.println("2. ⏳ 进行中");
            System.out.println("3. ✅ 已完成");
            System.out.println("4. ❌ 已取消");
            System.out.print("请选择: ");
            
            String statusChoice = scanner.nextLine();
            
            // 修正的switch语句 - 传统语法
            String status;
            switch (statusChoice) {
                case "1":
                    status = "待完成";
                    break;
                case "2":
                    status = "进行中";
                    break;
                case "3":
                    status = "已完成";
                    break;
                case "4":
                    status = "已取消";
                    break;
                default:
                    status = null;
                    break;
            }
            
            if (status != null && taskService.updateTaskStatus(taskId, status)) {
                System.out.println("✅ 任务状态更新成功");
            } else {
                System.out.println("❌ 任务状态更新失败");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 任务ID必须是数字");
        }
    }
    
    /**
     * 删除任务
     */
    private void deleteTask() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("                  删除任务");
        System.out.println("─".repeat(40));
        
        viewAllTasks();
        
        System.out.print("请输入要删除的任务ID: ");
        try {
            int taskId = Integer.parseInt(scanner.nextLine());
            
            System.out.print("确认删除任务 " + taskId + "? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                if (taskService.deleteTask(taskId)) {
                    System.out.println("✅ 任务删除成功");
                } else {
                    System.out.println("❌ 任务删除失败");
                }
            } else {
                System.out.println("已取消删除");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 任务ID必须是数字");
        }
    }
    
    /**
     * 显示任务统计
     */
    private void showTaskStatistics() {
        List<Task> allTasks = taskService.getUserTasks(currentUser.getUserId());
        List<Task> pendingTasks = taskService.getTasksByStatus(currentUser.getUserId(), "待完成");
        List<Task> todayTasks = taskService.getTodayTasks(currentUser.getUserId());
        
        System.out.println("\n" + "═".repeat(40));
        System.out.println("             任务统计");
        System.out.println("═".repeat(40));
        System.out.println("📊 总任务数: " + allTasks.size());
        System.out.println("📋 待完成: " + pendingTasks.size());
        System.out.println("📅 今日到期: " + todayTasks.size());
        
        if (!todayTasks.isEmpty()) {
            System.out.println("\n📢 今日到期任务提醒:");
            for (Task task : todayTasks) {
                System.out.println("  • " + task.getTitle() + " (" + getPriorityIcon(task.getPriority()) + ")");
            }
        }
        
        // 按状态统计
        long inProgress = allTasks.stream().filter(t -> "进行中".equals(t.getStatus())).count();
        long completed = allTasks.stream().filter(t -> "已完成".equals(t.getStatus())).count();
        long cancelled = allTasks.stream().filter(t -> "已取消".equals(t.getStatus())).count();
        
        System.out.println("\n📈 状态分布:");
        System.out.println("  ⏳ 进行中: " + inProgress);
        System.out.println("  ✅ 已完成: " + completed);
        System.out.println("  ❌ 已取消: " + cancelled);
    }
    
    /**
     * 显示用户信息
     */
    private void showUserInfo() {
        System.out.println("\n" + "═".repeat(40));
        System.out.println("             用户信息");
        System.out.println("═".repeat(40));
        System.out.println("👤 用户ID: " + currentUser.getUserId());
        System.out.println("👤 用户名: " + currentUser.getUsername());
        System.out.println("📧 邮箱: " + currentUser.getEmail());
        System.out.println("📅 注册时间: " + currentUser.getCreatedAt());
        System.out.println("═".repeat(40));
    }
    
    /**
     * 查看所有用户（管理员）
     */
    private void viewAllUsers() {
        System.out.println("\n所有用户列表:");
        // 这里需要添加UserDAO.getAllUsers()方法
    }
    
    /**
     * 查看所有任务（管理员）
     */
    private void viewAllTasksAdmin() {
        System.out.println("\n所有用户的任务:");
        // 这里需要添加TaskDAO.getAllTasks()方法
    }
    
    /**
     * 删除用户（管理员）
     */
    private void deleteUserAdmin() {
        System.out.print("请输入要删除的用户ID: ");
        // 管理员删除用户逻辑
    }
    
    /**
     * 显示系统统计（管理员）
     */
    private void showSystemStatistics() {
        System.out.println("\n系统统计信息:");
        // 系统统计逻辑
    }
    
    /**
     * 获取状态图标
     */
    private String getStatusIcon(String status) {
        String icon;
        switch (status) {
            case "待完成":
                icon = "🔄 ";
                break;
            case "进行中":
                icon = "⏳ ";
                break;
            case "已完成":
                icon = "✅ ";
                break;
            case "已取消":
                icon = "❌ ";
                break;
            default:
                icon = "📌 ";
                break;
        }
        return icon;
    }
    
    /**
     * 获取优先级图标
     */
    private String getPriorityIcon(String priority) {
        String icon;
        switch (priority) {
            case "高":
                icon = "🔥 ";
                break;
            case "中":
                icon = "⚡ ";
                break;
            case "低":
                icon = "🐌 ";
                break;
            default:
                icon = "📌 ";
                break;
        }
        return icon;
    }
    
    /**
     * 主方法 - 用于启动程序
     */
    public static void main(String[] args) {
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.showMainMenu();
    }
}