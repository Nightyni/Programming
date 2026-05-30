package com.taskmanagement.ui;

import com.taskmanagement.model.User;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.UserService;
import com.taskmanagement.service.TaskService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("all")
public class GraphicalUI {
    private JFrame frame;
    private UserService userService;
    private TaskService taskService;
    private User currentUser;
    
    // 组件
    private JTabbedPane tabbedPane;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    
    public GraphicalUI() {
        userService = new UserService();
        taskService = new TaskService();
        currentUser = null;
        initialize();
    }
    
    private void initialize() {
        frame = new JFrame("个人任务管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        
        showLoginPanel();
        frame.setVisible(true);
    }
    
    /**
     * 显示登录面板
     */
    private void showLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 标题
        JLabel titleLabel = new JLabel("个人任务管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 200));
        
        // 用户名
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JTextField userField = new JTextField(20);
        userField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 密码
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton loginBtn = new JButton("登录");
        JButton registerBtn = new JButton("注册");
        JButton exitBtn = new JButton("退出");
        
        // 按钮样式
        styleButton(loginBtn, new Color(70, 130, 180));
        styleButton(registerBtn, new Color(60, 179, 113));
        styleButton(exitBtn, new Color(220, 20, 60));
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(exitBtn);
        
        // 测试账号提示
        JLabel testLabel = new JLabel("测试账号: admin / admin123 或 user1 / user123", SwingConstants.CENTER);
        testLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        testLabel.setForeground(Color.GRAY);
        
        // 布局
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(testLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(passField, gbc);
        
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(buttonPanel, gbc);
        
        // 背景颜色
        panel.setBackground(new Color(240, 248, 255));
        
        // 事件监听
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            currentUser = userService.login(username, password);
            
            if (currentUser != null) {
                JOptionPane.showMessageDialog(frame, "登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                showMainPanel();
            } else {
                JOptionPane.showMessageDialog(frame, "登录失败，用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registerBtn.addActionListener(e -> showRegisterDialog());
        exitBtn.addActionListener(e -> System.exit(0));
        
        frame.setContentPane(panel);
        frame.revalidate();
    }
    
    /**
     * 显示注册对话框
     */
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(frame, "用户注册", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        
        JLabel userLabel = new JLabel("用户名:");
        JTextField userField = new JTextField(15);
        
        JLabel passLabel = new JLabel("密码:");
        JPasswordField passField = new JPasswordField(15);
        
        JLabel emailLabel = new JLabel("邮箱:");
        JTextField emailField = new JTextField(15);
        
        JButton registerBtn = new JButton("注册");
        JButton cancelBtn = new JButton("取消");
        
        // 布局
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridwidth = 2; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        // 事件监听
        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String email = emailField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (userService.register(username, password, email)) {
                JOptionPane.showMessageDialog(dialog, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "注册失败，用户名可能已存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    /**
     * 显示主面板
     */
    private void showMainPanel() {
        frame.setTitle("个人任务管理系统 - 用户: " + currentUser.getUsername());
        
        tabbedPane = new JTabbedPane();
        
        // 任务列表标签页
        JPanel taskPanel = createTaskPanel();
        tabbedPane.addTab("📋 我的任务", taskPanel);
        
        // 添加任务标签页
        JPanel addTaskPanel = createAddTaskPanel();
        tabbedPane.addTab("➕ 添加任务", addTaskPanel);
        
        // 统计标签页
        JPanel statsPanel = createStatsPanel();
        tabbedPane.addTab("📊 统计", statsPanel);
        
        // 用户信息标签页
        JPanel userPanel = createUserPanel();
        tabbedPane.addTab("👤 用户信息", userPanel);
        
        // 退出按钮
        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            showLoginPanel();
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutBtn);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.setContentPane(mainPanel);
        frame.revalidate();
    }
    
    /**
     * 创建任务面板
     */
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 表格列名
        String[] columns = {"ID", "标题", "状态", "优先级", "截止日期", "描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);
        taskTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        taskTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton refreshBtn = new JButton("刷新");
        JButton deleteBtn = new JButton("删除任务");
        JButton updateBtn = new JButton("更新状态");
        
        refreshBtn.addActionListener(e -> refreshTaskTable());
        deleteBtn.addActionListener(this::deleteSelectedTask);
        updateBtn.addActionListener(this::updateTaskStatus);
        
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(deleteBtn);
        toolBar.addSeparator();
        toolBar.add(updateBtn);
        
        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        refreshTaskTable();
        return panel;
    }
    
    /**
     * 刷新任务表格
     */
    private void refreshTaskTable() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskService.getUserTasks(currentUser.getUserId());
        
        for (Task task : tasks) {
            Object[] row = {
                task.getTaskId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getDescription()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * 删除选中任务
     */
    private void deleteSelectedTask(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            String taskTitle = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "确定要删除任务: " + taskTitle + "?", 
                "确认删除", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (taskService.deleteTask(taskId)) {
                    JOptionPane.showMessageDialog(frame, "任务删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    refreshTaskTable();
                } else {
                    JOptionPane.showMessageDialog(frame, "任务删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "请先选择一个任务", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * 更新任务状态
     */
    private void updateTaskStatus(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            
            String[] statusOptions = {"待完成", "进行中", "已完成", "已取消"};
            String selectedStatus = (String) JOptionPane.showInputDialog(frame,
                "选择新的任务状态:", "更新状态",
                JOptionPane.QUESTION_MESSAGE, null,
                statusOptions, statusOptions[0]);
            
            if (selectedStatus != null && taskService.updateTaskStatus(taskId, selectedStatus)) {
                JOptionPane.showMessageDialog(frame, "状态更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshTaskTable();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "请先选择一个任务", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * 创建添加任务面板
     */
    private JPanel createAddTaskPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("添加新任务", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        
        JLabel nameLabel = new JLabel("任务标题:");
        JTextField titleField = new JTextField(20);
        
        JLabel descLabel = new JLabel("任务描述:");
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        
        JLabel priorityLabel = new JLabel("优先级:");
        String[] priorities = {"低", "中", "高"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        
        JLabel dateLabel = new JLabel("截止日期:");
        JTextField dateField = new JTextField(LocalDate.now().plusDays(7).toString());
        
        JButton addBtn = new JButton("添加任务");
        styleButton(addBtn, new Color(60, 179, 113));
        
        // 布局
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(priorityLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(priorityCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridwidth = 2; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addBtn, gbc);
        
        // 事件监听
        addBtn.addActionListener(e -> {
            String title = titleField.getText();
            String description = descArea.getText();
            String priority = (String) priorityCombo.getSelectedItem();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "任务标题不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                LocalDate dueDate = LocalDate.parse(dateField.getText());
                boolean success = taskService.createTask(currentUser.getUserId(), title, 
                                                        description, priority, dueDate);
                
                if (success) {
                    JOptionPane.showMessageDialog(frame, "任务添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    titleField.setText("");
                    descArea.setText("");
                    dateField.setText(LocalDate.now().plusDays(7).toString());
                    refreshTaskTable();
                    tabbedPane.setSelectedIndex(0); // 切换到任务列表
                } else {
                    JOptionPane.showMessageDialog(frame, "任务添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "日期格式错误，请使用 YYYY-MM-DD 格式", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    /**
     * 创建统计面板
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea statsArea = new JTextArea();
        statsArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statsArea.setEditable(false);
        
        JButton refreshBtn = new JButton("刷新统计");
        refreshBtn.addActionListener(e -> updateStats(statsArea));
        
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        updateStats(statsArea);
        return panel;
    }
    
    /**
     * 更新统计信息
     */
    private void updateStats(JTextArea statsArea) {
        List<Task> allTasks = taskService.getUserTasks(currentUser.getUserId());
        List<Task> pendingTasks = taskService.getTasksByStatus(currentUser.getUserId(), "待完成");
        List<Task> todayTasks = taskService.getTodayTasks(currentUser.getUserId());
        
        StringBuilder stats = new StringBuilder();
        stats.append("            任务统计报告\n");
        stats.append("═══════════════════════════════════\n\n");
        stats.append("📊 总任务数: ").append(allTasks.size()).append("\n");
        stats.append("📋 待完成: ").append(pendingTasks.size()).append("\n");
        stats.append("📅 今日到期: ").append(todayTasks.size()).append("\n\n");
        
        if (!todayTasks.isEmpty()) {
            stats.append("📢 今日到期任务提醒:\n");
            for (Task task : todayTasks) {
                stats.append("  • ").append(task.getTitle()).append(" (").append(task.getPriority()).append("优先级)\n");
            }
            stats.append("\n");
        }
        
        // 状态分布
        long inProgress = allTasks.stream().filter(t -> "进行中".equals(t.getStatus())).count();
        long completed = allTasks.stream().filter(t -> "已完成".equals(t.getStatus())).count();
        long cancelled = allTasks.stream().filter(t -> "已取消".equals(t.getStatus())).count();
        
        stats.append("📈 状态分布:\n");
        stats.append("  ⏳ 进行中: ").append(inProgress).append("\n");
        stats.append("  ✅ 已完成: ").append(completed).append("\n");
        stats.append("  ❌ 已取消: ").append(cancelled).append("\n");
        
        statsArea.setText(stats.toString());
    }
    
    /**
     * 创建用户信息面板
     */
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea userInfo = new JTextArea();
        userInfo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userInfo.setEditable(false);
        
        StringBuilder info = new StringBuilder();
        info.append("            用户信息\n");
        info.append("═══════════════════════════════════\n\n");
        info.append("👤 用户ID: ").append(currentUser.getUserId()).append("\n");
        info.append("👤 用户名: ").append(currentUser.getUsername()).append("\n");
        info.append("📧 邮箱: ").append(currentUser.getEmail() != null ? currentUser.getEmail() : "未设置").append("\n");
        info.append("📅 注册时间: ").append(currentUser.getCreatedAt()).append("\n\n");
        
        List<Task> tasks = taskService.getUserTasks(currentUser.getUserId());
        info.append("📊 个人任务统计:\n");
        info.append("  总任务数: ").append(tasks.size()).append("\n");
        
        userInfo.setText(info.toString());
        
        panel.add(new JScrollPane(userInfo), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * 美化按钮
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}