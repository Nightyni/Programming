package com.example.ui;

import com.example.dao.ProjectDAO;
import com.example.dao.UserDAO;
import com.example.model.Project;
import com.example.model.User;
import com.example.util.StyleUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProjectPanel extends JPanel {
    private User currentUser;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public ProjectPanel(User user) {
        this.currentUser = user;
        this.projectDAO = new ProjectDAO();
        this.userDAO = new UserDAO();
        initializeUI();
        loadProjectData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 创建顶部工具栏
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // 创建表格
        createProjectTable();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(new Color(240, 240, 245));
        
        // 搜索框
        toolbar.add(new JLabel("搜索:"));
        searchField = new JTextField(15);
        searchField.addActionListener(e -> searchProjects());
        toolbar.add(searchField);
        
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchProjects());
        toolbar.add(searchButton);
        
        // 状态筛选
        toolbar.add(new JLabel("状态:"));
        String[] statuses = {"全部", "进行中", "已结题", "申请中"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> filterProjects());
        toolbar.add(statusFilter);
        
        // 刷新按钮
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadProjectData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createProjectTable() {
        // 创建表格模型
        String[] columns = {"ID", "项目名称", "项目编号", "负责人", "经费(万元)", "开始日期", "结束日期", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectTable = new JTable(tableModel);
        projectTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        projectTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        projectTable.setRowHeight(25);
        projectTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        projectTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        projectTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        projectTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        projectTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        projectTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        // 设置状态列渲染器
        projectTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("项目列表"));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        addButton = new JButton("新增项目");
        editButton = new JButton("编辑项目");
        deleteButton = new JButton("删除项目");
    
        
        // 根据角色设置按钮可用性
        if (currentUser.getRole().equals("student")) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
        
        // 设置按钮样式
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.black);
        addButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        editButton.setBackground(new Color(0, 120, 215));
        editButton.setForeground(Color.black);
        editButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.black);
        deleteButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        // 添加事件监听
        addButton.addActionListener(e -> showAddProjectDialog());
        editButton.addActionListener(e -> showEditProjectDialog());
        deleteButton.addActionListener(e -> deleteProject());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        // 添加双击编辑
        projectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditProjectDialog();
                }
            }
        });
        
        return panel;
    }
    
    private void loadProjectData() {
        tableModel.setRowCount(0);
        
        List<Project> projects;
        if (currentUser.getRole().equals("admin")) {
            // 管理员看所有项目
            projects = projectDAO.getAllProjects();
        } else if (currentUser.getRole().equals("teacher")) {
            // 教师看自己负责的项目
            projects = projectDAO.getProjectsByLeader(currentUser.getId());
        } else {
            // 学生看参与的项目
            projects = projectDAO.getProjectsByMember(currentUser.getId());
        }
        
        for (Project project : projects) {
            Object[] row = {
                project.getId(),
                project.getProjectName(),
                project.getProjectNumber(),
                project.getLeaderName(),
                project.getBudget(),
                project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "",
                project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "",
                project.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchProjects() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadProjectData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Project> projects = projectDAO.getAllProjects();
        
        for (Project project : projects) {
            if (project.getProjectName().toLowerCase().contains(keyword) ||
                project.getProjectNumber().toLowerCase().contains(keyword) ||
                (project.getLeaderName() != null && project.getLeaderName().toLowerCase().contains(keyword))) {
                Object[] row = {
                    project.getId(),
                    project.getProjectName(),
                    project.getProjectNumber(),
                    project.getLeaderName(),
                    project.getBudget(),
                    project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "",
                    project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "",
                    project.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void filterProjects() {
        String status = (String) statusFilter.getSelectedItem();
        if ("全部".equals(status)) {
            loadProjectData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Project> projects = projectDAO.getAllProjects();
        
        for (Project project : projects) {
            if (status.equals(project.getStatus())) {
                Object[] row = {
                    project.getId(),
                    project.getProjectName(),
                    project.getProjectNumber(),
                    project.getLeaderName(),
                    project.getBudget(),
                    project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "",
                    project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "",
                    project.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showAddProjectDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "新增项目", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 项目名称
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目名称:*"), gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // 项目编号
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目编号:*"), gbc);
        
        gbc.gridx = 1;
        JTextField numberField = new JTextField(20);
        panel.add(numberField, gbc);
        
        // 负责人
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("负责人:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<User> leaderCombo = new JComboBox<>();
        List<User> teachers = userDAO.getUsersByRole("teacher");
        for (User teacher : teachers) {
            leaderCombo.addItem(teacher);
        }
        panel.add(leaderCombo, gbc);
        
        // 经费
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("经费(万元):"), gbc);
        
        gbc.gridx = 1;
        JTextField budgetField = new JTextField(20);
        panel.add(budgetField, gbc);
        
        // 开始日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("开始日期:"), gbc);
        
        gbc.gridx = 1;
        JTextField startDateField = new JTextField(20);
        startDateField.setText(dateFormat.format(new Date()));
        panel.add(startDateField, gbc);
        
        // 结束日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("结束日期:"), gbc);
        
        gbc.gridx = 1;
        JTextField endDateField = new JTextField(20);
        panel.add(endDateField, gbc);
        
        // 状态
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("状态:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"申请中", "进行中", "已结题"});
        panel.add(statusCombo, gbc);
        
        // 描述
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目描述:"), gbc);
        
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            // 验证输入
            if (nameField.getText().trim().isEmpty() || numberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "项目名称和项目编号不能为空！");
                return;
            }
            
            try {
                Project project = new Project();
                project.setProjectName(nameField.getText().trim());
                project.setProjectNumber(numberField.getText().trim());
                
                User selectedLeader = (User) leaderCombo.getSelectedItem();
                if (selectedLeader != null) {
                    project.setLeaderId(selectedLeader.getId());
                }
                
                if (!budgetField.getText().trim().isEmpty()) {
                    project.setBudget(Double.parseDouble(budgetField.getText().trim()));
                }
                
                try {
                    if (!startDateField.getText().trim().isEmpty()) {
                        project.setStartDate(dateFormat.parse(startDateField.getText().trim()));
                    }
                    if (!endDateField.getText().trim().isEmpty()) {
                        project.setEndDate(dateFormat.parse(endDateField.getText().trim()));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                    return;
                }
                
                project.setStatus((String) statusCombo.getSelectedItem());
                project.setDescription(descArea.getText().trim());
                
                if (projectDAO.addProject(project)) {
                    JOptionPane.showMessageDialog(dialog, "项目添加成功！");
                    dialog.dispose();
                    loadProjectData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败，请重试！");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "经费必须为数字！");
            }
        });
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showEditProjectDialog() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个项目！");
            return;
        }
        
        int projectId = (int) tableModel.getValueAt(selectedRow, 0);
        Project project = projectDAO.getProjectById(projectId);
        
        if (project == null) {
            JOptionPane.showMessageDialog(this, "获取项目信息失败！");
            return;
        }
        
        // 类似新增项目的对话框，但填充现有数据
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑项目", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 项目名称
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目名称:*"), gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(project.getProjectName(), 20);
        panel.add(nameField, gbc);
        
        // 项目编号
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目编号:*"), gbc);
        
        gbc.gridx = 1;
        JTextField numberField = new JTextField(project.getProjectNumber(), 20);
        panel.add(numberField, gbc);
        
        // 负责人
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("负责人:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<User> leaderCombo = new JComboBox<>();
        List<User> teachers = userDAO.getUsersByRole("teacher");
        for (User teacher : teachers) {
            leaderCombo.addItem(teacher);
            if (teacher.getId() == project.getLeaderId()) {
                leaderCombo.setSelectedItem(teacher);
            }
        }
        panel.add(leaderCombo, gbc);
        
        // 经费
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("经费(万元):"), gbc);
        
        gbc.gridx = 1;
        JTextField budgetField = new JTextField(String.valueOf(project.getBudget()), 20);
        panel.add(budgetField, gbc);
        
        // 开始日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("开始日期:"), gbc);
        
        gbc.gridx = 1;
        JTextField startDateField = new JTextField(
            project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "", 20);
        panel.add(startDateField, gbc);
        
        // 结束日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("结束日期:"), gbc);
        
        gbc.gridx = 1;
        JTextField endDateField = new JTextField(
            project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "", 20);
        panel.add(endDateField, gbc);
        
        // 状态
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("状态:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"申请中", "进行中", "已结题"});
        statusCombo.setSelectedItem(project.getStatus());
        panel.add(statusCombo, gbc);
        
        // 描述
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("项目描述:"), gbc);
        
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(project.getDescription(), 5, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            project.setProjectName(nameField.getText().trim());
            project.setProjectNumber(numberField.getText().trim());
            
            User selectedLeader = (User) leaderCombo.getSelectedItem();
            if (selectedLeader != null) {
                project.setLeaderId(selectedLeader.getId());
            }
            
            try {
                if (!budgetField.getText().trim().isEmpty()) {
                    project.setBudget(Double.parseDouble(budgetField.getText().trim()));
                }
                
                try {
                    if (!startDateField.getText().trim().isEmpty()) {
                        project.setStartDate(dateFormat.parse(startDateField.getText().trim()));
                    }
                    if (!endDateField.getText().trim().isEmpty()) {
                        project.setEndDate(dateFormat.parse(endDateField.getText().trim()));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                    return;
                }
                
                project.setStatus((String) statusCombo.getSelectedItem());
                project.setDescription(descArea.getText().trim());
                
                if (projectDAO.updateProject(project)) {
                    JOptionPane.showMessageDialog(dialog, "项目更新成功！");
                    dialog.dispose();
                    loadProjectData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "更新失败，请重试！");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "经费必须为数字！");
            }
        });
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个项目！");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除该项目吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int projectId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (projectDAO.deleteProject(projectId)) {
                JOptionPane.showMessageDialog(this, "项目删除成功！");
                loadProjectData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请重试！");
            }
        }
    }
    
    // 状态列渲染器
    class StatusCellRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
            setFont(new Font("微软雅黑", Font.PLAIN, 12));
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText(value != null ? value.toString() : "");
            
            if (!isSelected) {
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "进行中":
                        setBackground(new Color(40, 167, 69, 50));
                        setForeground(new Color(0, 100, 0));
                        break;
                    case "已结题":
                        setBackground(new Color(0, 123, 255, 50));
                        setForeground(new Color(0, 0, 150));
                        break;
                    case "申请中":
                        setBackground(new Color(255, 193, 7, 50));
                        setForeground(new Color(150, 100, 0));
                        break;
                    default:
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                }
            } else {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            return this;
        }
    }
}