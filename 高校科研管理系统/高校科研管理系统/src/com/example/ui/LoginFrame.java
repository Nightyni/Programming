package com.example.ui;

import com.example.dao.UserDAO;
import com.example.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("高校科研管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);
        
        // 设置整体样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // 标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 245, 250));
        
        // 系统标题
        JLabel titleLabel = new JLabel("高校科研管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 副标题
        JLabel subtitleLabel = new JLabel("Research Management System", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // 用户名标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(usernameField, gbc);
        
        // 密码标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        loginButton = new JButton("登 录");
        registerButton = new JButton("注 册");
        
        // 设置按钮样式
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // 鼠标悬停效果
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 100, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 120, 215));
            }
        });
        
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(100, 40));
        registerButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(30, 140, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(40, 167, 69));
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // 底部信息
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(245, 245, 250));
        JLabel versionLabel = new JLabel("Version 1.0 | 高校科研管理系统");
        versionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        versionLabel.setForeground(Color.GRAY);
        bottomPanel.add(versionLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 添加事件监听器
        addEventListeners();
        
        // 设置回车键登录
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void addEventListeners() {
        // 登录按钮事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                // 输入验证
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
                    usernameField.requestFocus();
                    return;
                }
                
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
                    passwordField.requestFocus();
                    return;
                }
                
                // 显示登录中提示
                loginButton.setEnabled(false);
                loginButton.setText("登录中...");
                
                // 在后台线程执行登录操作
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        Thread.sleep(500); // 模拟网络延迟
                        return userDAO.loginUser(username, password);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean loginSuccess = get();
                            
                            if (loginSuccess) {
                                // 获取用户完整信息
                                User user = userDAO.getUserByUsername(username);
                                
                                JOptionPane.showMessageDialog(LoginFrame.this, 
                                    "登录成功！欢迎 " + user.getRealName(), 
                                    "成功", JOptionPane.INFORMATION_MESSAGE);
                                
                                // 打开主界面，关闭登录窗口
                                new MainFrame(user).setVisible(true);
                                LoginFrame.this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(LoginFrame.this, 
                                    "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                                passwordField.setText("");
                                passwordField.requestFocus();
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(LoginFrame.this, 
                                "登录失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        } finally {
                            loginButton.setEnabled(true);
                            loginButton.setText("登 录");
                        }
                    }
                };
                worker.execute();
            }
        });
        
        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开注册窗口
                new RegisterFrame().setVisible(true);
                // 可选择是否关闭登录窗口
                // LoginFrame.this.dispose();
            }
        });
        
        // 添加键盘监听（回车登录）
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
    
    // 键盘适配器内部类
    private abstract class KeyAdapter implements java.awt.event.KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}
        
        @Override
        public void keyPressed(KeyEvent e) {}
        
        @Override
        public void keyReleased(KeyEvent e) {}
    }
}