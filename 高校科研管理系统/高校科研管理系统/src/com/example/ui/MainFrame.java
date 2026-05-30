package com.example.ui;

import com.example.model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private JLabel userInfoLabel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("高校科研管理系统 - 欢迎 " + currentUser.getRealName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // 设置整体样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建顶部工具栏
        JPanel toolbar = createToolbar();
        mainPanel.add(toolbar, BorderLayout.NORTH);
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 根据角色显示不同的功能
        if (currentUser.getRole().equals("admin")) {
            // 管理员可以看到所有功能
            tabbedPane.addTab("项目管理", new ProjectPanel(currentUser));
            tabbedPane.addTab("论文管理", new PaperPanel(currentUser));
            tabbedPane.addTab("团队管理", new TeamPanel(currentUser));
            // 移除用户管理和统计分析
        } else if (currentUser.getRole().equals("teacher")) {
            // 教师可以看到自己的项目和论文
            tabbedPane.addTab("我的项目", new ProjectPanel(currentUser));
            tabbedPane.addTab("我的论文", new PaperPanel(currentUser));
            tabbedPane.addTab("我的团队", new TeamPanel(currentUser));
        } else {
            // 学生只能查看参与的项目和论文
            tabbedPane.addTab("参与项目", new ProjectPanel(currentUser));
            tabbedPane.addTab("我的论文", new PaperPanel(currentUser));
            tabbedPane.addTab("我的团队", new TeamPanel(currentUser));
        }
        
        // 个人信息面板（所有人可见）
        tabbedPane.addTab("个人信息", new ProfilePanel(currentUser));
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // 创建状态栏
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 创建菜单栏
        createMenuBar();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(240, 240, 245));
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // 左侧 Logo 和标题
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(240, 240, 245));
        
        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        leftPanel.add(logoLabel);
        
        JLabel titleLabel = new JLabel("高校科研管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        leftPanel.add(titleLabel);
        
        toolbar.add(leftPanel, BorderLayout.WEST);
        
        // 右侧用户信息
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(240, 240, 245));
        
        userInfoLabel = new JLabel(currentUser.getRealName() + " (" + currentUser.getRoleDisplayName() + ")");
        userInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        rightPanel.add(userInfoLabel);
        
        JButton logoutBtn = new JButton("注销");
        logoutBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutBtn.addActionListener(e -> logout());
        rightPanel.add(logoutBtn);
        
        toolbar.add(rightPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 245));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("就绪 | 当前时间: " + new java.util.Date().toLocaleString());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        systemMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        JMenuItem changePwdItem = new JMenuItem("修改密码");
        changePwdItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        changePwdItem.addActionListener(e -> showChangePasswordDialog());
        
        JMenuItem logoutItem = new JMenuItem("注销");
        logoutItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        logoutItem.addActionListener(e -> logout());
        
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitItem.addActionListener(e -> System.exit(0));
        
        systemMenu.add(changePwdItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);
        
        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(systemMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "修改密码", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 旧密码
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("旧密码:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField oldPwdField = new JPasswordField(15);
        panel.add(oldPwdField, gbc);
        
        // 新密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("新密码:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField newPwdField = new JPasswordField(15);
        panel.add(newPwdField, gbc);
        
        // 确认新密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("确认新密码:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField confirmPwdField = new JPasswordField(15);
        panel.add(confirmPwdField, gbc);
        
        // 按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton confirmBtn = new JButton("确认");
        confirmBtn.addActionListener(e -> {
            String oldPwd = new String(oldPwdField.getPassword());
            String newPwd = new String(newPwdField.getPassword());
            String confirmPwd = new String(confirmPwdField.getPassword());
            
            if (!newPwd.equals(confirmPwd)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的新密码不一致");
                return;
            }
            
            // TODO: 调用UserDAO修改密码
            JOptionPane.showMessageDialog(dialog, "密码修改成功");
            dialog.dispose();
        });
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAboutDialog() {
        String message = "高校科研管理系统\n" + 
                        "版本: 1.0\n" + 
                        "开发工具: Java Swing\n" + 
                        "数据库: MySQL\n\n" + 
                        "© 2024 科研管理系统";
        
        JOptionPane.showMessageDialog(this, message, "关于", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要注销吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}