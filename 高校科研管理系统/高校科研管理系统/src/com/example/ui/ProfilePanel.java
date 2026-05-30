package com.example.ui;

import com.example.dao.UserDAO;
import com.example.model.User;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private User currentUser;
    private UserDAO userDAO;
    
    private JTextField emailField;
    private JTextField realNameField;
    private JTextField departmentField;
    private JTextField titleField;
    private JLabel usernameLabel;
    private JLabel roleLabel;
    
    public ProfilePanel(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建标题
        JLabel titleLabel = new JLabel("个人信息");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "基本信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // 用户名（只读）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("用户名:"), gbc);
        
        gbc.gridx = 1;
        usernameLabel = new JLabel(currentUser.getUsername());
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        infoPanel.add(usernameLabel, gbc);
        
        // 角色（只读）
        gbc.gridx = 2;
        infoPanel.add(new JLabel("角色:"), gbc);
        
        gbc.gridx = 3;
        roleLabel = new JLabel(currentUser.getRoleDisplayName());
        roleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        infoPanel.add(roleLabel, gbc);
        
        // 真实姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("真实姓名:"), gbc);
        
        gbc.gridx = 1;
        realNameField = new JTextField(currentUser.getRealName(), 15);
        infoPanel.add(realNameField, gbc);
        
        // 邮箱
        gbc.gridx = 2;
        infoPanel.add(new JLabel("邮箱:"), gbc);
        
        gbc.gridx = 3;
        emailField = new JTextField(currentUser.getEmail(), 15);
        infoPanel.add(emailField, gbc);
        
        // 院系
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("院系:"), gbc);
        
        gbc.gridx = 1;
        departmentField = new JTextField(currentUser.getDepartment(), 15);
        infoPanel.add(departmentField, gbc);
        
        // 职称
        gbc.gridx = 2;
        infoPanel.add(new JLabel("职称:"), gbc);
        
        gbc.gridx = 3;
        titleField = new JTextField(currentUser.getTitle(), 15);
        infoPanel.add(titleField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton saveButton = new JButton("保存修改");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.black);
        saveButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> saveProfile());
        
        JButton refreshButton = new JButton("刷新");
        refreshButton.setBackground(new Color(0, 120, 215));
        refreshButton.setForeground(Color.black);
        refreshButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.addActionListener(e -> refreshProfile());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        infoPanel.add(buttonPanel, gbc);
        
        add(infoPanel, BorderLayout.CENTER);
        
        // 创建统计信息面板
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBorder(BorderFactory.createTitledBorder("统计信息"));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // 项目统计
        JPanel projectStats = new JPanel(new BorderLayout());
        projectStats.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        projectStats.setBackground(new Color(240, 248, 255));
        
        JLabel projectIcon = new JLabel("📊", JLabel.CENTER);
        projectIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        projectStats.add(projectIcon, BorderLayout.WEST);
        
        JPanel projectText = new JPanel(new GridLayout(2, 1));
        projectText.setBackground(new Color(240, 248, 255));
        projectText.add(new JLabel("参与项目", JLabel.CENTER));
        JLabel projectCount = new JLabel("3", JLabel.CENTER);
        projectCount.setFont(new Font("微软雅黑", Font.BOLD, 20));
        projectText.add(projectCount);
        projectStats.add(projectText, BorderLayout.CENTER);
        
        // 论文统计
        JPanel paperStats = new JPanel(new BorderLayout());
        paperStats.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        paperStats.setBackground(new Color(255, 240, 245));
        
        JLabel paperIcon = new JLabel("📝", JLabel.CENTER);
        paperIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        paperStats.add(paperIcon, BorderLayout.WEST);
        
        JPanel paperText = new JPanel(new GridLayout(2, 1));
        paperText.setBackground(new Color(255, 240, 245));
        paperText.add(new JLabel("发表论文", JLabel.CENTER));
        JLabel paperCount = new JLabel("5", JLabel.CENTER);
        paperCount.setFont(new Font("微软雅黑", Font.BOLD, 20));
        paperText.add(paperCount);
        paperStats.add(paperText, BorderLayout.CENTER);
        
        // 团队统计
        JPanel teamStats = new JPanel(new BorderLayout());
        teamStats.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        teamStats.setBackground(new Color(240, 255, 240));
        
        JLabel teamIcon = new JLabel("👥", JLabel.CENTER);
        teamIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        teamStats.add(teamIcon, BorderLayout.WEST);
        
        JPanel teamText = new JPanel(new GridLayout(2, 1));
        teamText.setBackground(new Color(240, 255, 240));
        teamText.add(new JLabel("所属团队", JLabel.CENTER));
        JLabel teamCount = new JLabel("2", JLabel.CENTER);
        teamCount.setFont(new Font("微软雅黑", Font.BOLD, 20));
        teamText.add(teamCount);
        teamStats.add(teamText, BorderLayout.CENTER);
        
        panel.add(projectStats);
        panel.add(paperStats);
        panel.add(teamStats);
        
        return panel;
    }
    
    private void saveProfile() {
        // 更新用户信息
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setRealName(realNameField.getText().trim());
        currentUser.setDepartment(departmentField.getText().trim());
        currentUser.setTitle(titleField.getText().trim());
        
        if (userDAO.updateUser(currentUser)) {
            JOptionPane.showMessageDialog(this, "个人信息更新成功！", "成功", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // 更新主界面标题
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).setTitle("高校科研管理系统 - 欢迎 " + currentUser.getRealName());
            }
        } else {
            JOptionPane.showMessageDialog(this, "更新失败，请重试！", "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshProfile() {
        // 从数据库重新获取用户信息
        User refreshedUser = userDAO.getUserById(currentUser.getId());
        if (refreshedUser != null) {
            currentUser = refreshedUser;
            
            // 更新界面
            usernameLabel.setText(currentUser.getUsername());
            roleLabel.setText(currentUser.getRoleDisplayName());
            realNameField.setText(currentUser.getRealName());
            emailField.setText(currentUser.getEmail());
            departmentField.setText(currentUser.getDepartment());
            titleField.setText(currentUser.getTitle());
            
            JOptionPane.showMessageDialog(this, "信息已刷新", "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}