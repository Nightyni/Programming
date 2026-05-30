package com.example.ui;

import com.example.dao.UserDAO;
import com.example.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField, emailField, realNameField, departmentField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JButton registerButton, backButton;
    private UserDAO userDAO;
    
    public RegisterFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("用户注册 - 高校科研管理系统");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("用户注册", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 用户名
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("用户名:*");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // 密码
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("密码:*");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // 确认密码
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel confirmLabel = new JLabel("确认密码:*");
        confirmLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(confirmPasswordField, gbc);
        
        // 邮箱
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel emailLabel = new JLabel("邮箱:*");
        emailLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(15);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);
        
        // 真实姓名
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel realNameLabel = new JLabel("真实姓名:");
        realNameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(realNameLabel, gbc);
        
        gbc.gridx = 1;
        realNameField = new JTextField(15);
        realNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(realNameField, gbc);
        
        // 身份角色
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel roleLabel = new JLabel("身份角色:");
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        String[] roles = {"学生", "教师"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(roleCombo, gbc);
        
        // 院系
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel deptLabel = new JLabel("院系:");
        deptLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(deptLabel, gbc);
        
        gbc.gridx = 1;
        String[] departments = {"计算机学院", "软件学院", "信息学院", "人工智能学院", "其他"};
        departmentField = new JTextField(15);
        departmentField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(departmentField, gbc);
        
        // 按钮面板
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        registerButton = new JButton("注 册");
        backButton = new JButton("返回登录");
        
        // 设置按钮样式
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(100, 40));
        
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.black);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(100, 40));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // 提示信息
        JLabel noteLabel = new JLabel("* 为必填项", JLabel.RIGHT);
        noteLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        noteLabel.setForeground(Color.GRAY);
        mainPanel.add(noteLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 添加事件监听器
        addEventListeners();
    }
    
    private void addEventListeners() {
        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String realName = realNameField.getText().trim();
                String role = (String) roleCombo.getSelectedItem();
                String department = departmentField.getText().trim();
                
                // 转换为数据库存储的格式
                String dbRole = "student"; // 默认学生
                if ("教师".equals(role)) {
                    dbRole = "teacher";
                }
                
                // 输入验证
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "密码长度至少6位", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 检查用户名是否已存在
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "用户名已存在，请选择其他用户名", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 创建用户并注册
                User user = new User(username, password, email);
                user.setRealName(realName.isEmpty() ? username : realName);
                user.setRole(dbRole);
                user.setDepartment(department);
                
                if (userDAO.registerUser(user)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                    RegisterFrame.this.dispose(); // 关闭注册窗口
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, 
                        "注册失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 返回按钮事件
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame.this.dispose(); // 关闭注册窗口
            }
        });
    }
}