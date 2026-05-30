package com.example.ui;

import com.example.dao.TeamDAO;
import com.example.dao.UserDAO;
import com.example.model.Team;
import com.example.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TeamPanel extends JPanel {
    private User currentUser;
    private TeamDAO teamDAO;
    private UserDAO userDAO;
    
    private JTable teamTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, viewMembersButton, refreshButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public TeamPanel(User user) {
        this.currentUser = user;
        this.teamDAO = new TeamDAO();
        this.userDAO = new UserDAO();
        initializeUI();
        loadTeamData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 创建顶部工具栏
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // 创建表格
        createTeamTable();
        
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
        searchField.addActionListener(e -> searchTeams());
        toolbar.add(searchField);
        
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchTeams());
        toolbar.add(searchButton);
        
        // 刷新按钮
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadTeamData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTeamTable() {
        // 创建表格模型
        String[] columns = {"ID", "团队名称", "负责人", "成立日期", "研究方向", "成立年限"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        teamTable = new JTable(tableModel);
        teamTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        teamTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        teamTable.setRowHeight(25);
        teamTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        teamTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        teamTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        teamTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        teamTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        teamTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        teamTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(teamTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("科研团队列表"));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        addButton = new JButton("新建团队");
        editButton = new JButton("编辑团队");
        deleteButton = new JButton("解散团队");
        viewMembersButton = new JButton("查看成员");
        
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
        
        viewMembersButton.setBackground(new Color(108, 117, 125));
        viewMembersButton.setForeground(Color.black);
        viewMembersButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        // 添加事件监听
        addButton.addActionListener(e -> showAddTeamDialog());
        editButton.addActionListener(e -> showEditTeamDialog());
        deleteButton.addActionListener(e -> deleteTeam());
        viewMembersButton.addActionListener(e -> showTeamMembers());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(viewMembersButton);
        
        // 添加双击编辑
        teamTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showTeamMembers();
                }
            }
        });
        
        return panel;
    }
    
    private void loadTeamData() {
        tableModel.setRowCount(0);
        
        List<Team> teams;
        if (currentUser.getRole().equals("admin")) {
            // 管理员看所有团队
            teams = teamDAO.getAllTeams();
        } else if (currentUser.getRole().equals("teacher")) {
            // 教师看自己负责的团队和参与的团队
            teams = teamDAO.getTeamsByLeader(currentUser.getId());
            List<Team> memberTeams = teamDAO.getTeamsByMember(currentUser.getId());
            for (Team team : memberTeams) {
                if (!teams.contains(team)) {
                    teams.add(team);
                }
            }
        } else {
            // 学生看参与的团队
            teams = teamDAO.getTeamsByMember(currentUser.getId());
        }
        
        for (Team team : teams) {
            Object[] row = {
                team.getId(),
                team.getTeamName(),
                team.getLeaderName(),
                team.getEstablishedDate() != null ? dateFormat.format(team.getEstablishedDate()) : "",
                team.getResearchArea(),
                team.getYearsEstablished() + "年"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchTeams() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadTeamData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Team> teams = teamDAO.getAllTeams();
        
        for (Team team : teams) {
            if (team.getTeamName().toLowerCase().contains(keyword) ||
                team.getLeaderName().toLowerCase().contains(keyword) ||
                (team.getResearchArea() != null && team.getResearchArea().toLowerCase().contains(keyword))) {
                Object[] row = {
                    team.getId(),
                    team.getTeamName(),
                    team.getLeaderName(),
                    team.getEstablishedDate() != null ? dateFormat.format(team.getEstablishedDate()) : "",
                    team.getResearchArea(),
                    team.getYearsEstablished() + "年"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showAddTeamDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "新建科研团队", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 团队名称
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("团队名称:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // 负责人
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("负责人:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JComboBox<User> leaderCombo = new JComboBox<>();
        List<User> teachers = userDAO.getUsersByRole("teacher");
        for (User teacher : teachers) {
            leaderCombo.addItem(teacher);
            if (teacher.getId() == currentUser.getId()) {
                leaderCombo.setSelectedItem(teacher);
            }
        }
        panel.add(leaderCombo, gbc);
        
        // 成立日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("成立日期:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField dateField = new JTextField(dateFormat.format(new Date()), 20);
        panel.add(dateField, gbc);
        
        // 研究方向
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("研究方向:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField areaField = new JTextField(20);
        panel.add(areaField, gbc);
        
        // 团队描述
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("团队描述:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("创建团队");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || areaField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "团队名称和研究方向不能为空！");
                return;
            }
            
            Team team = new Team();
            team.setTeamName(nameField.getText().trim());
            
            User selectedLeader = (User) leaderCombo.getSelectedItem();
            if (selectedLeader != null) {
                team.setLeaderId(selectedLeader.getId());
            }
            
            try {
                if (!dateField.getText().trim().isEmpty()) {
                    team.setEstablishedDate(dateFormat.parse(dateField.getText().trim()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                return;
            }
            
            team.setResearchArea(areaField.getText().trim());
            team.setDescription(descArea.getText().trim());
            
            if (teamDAO.addTeam(team)) {
                JOptionPane.showMessageDialog(dialog, "团队创建成功！");
                
                // 自动将负责人添加为团队成员
                if (selectedLeader != null) {
                    teamDAO.addTeamMember(team.getId(), selectedLeader.getId(), "负责人");
                }
                
                dialog.dispose();
                loadTeamData();
            } else {
                JOptionPane.showMessageDialog(dialog, "创建失败，请重试！");
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
    
    private void showEditTeamDialog() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个团队！");
            return;
        }
        
        int teamId = (int) tableModel.getValueAt(selectedRow, 0);
        Team team = teamDAO.getTeamById(teamId);
        
        if (team == null) {
            JOptionPane.showMessageDialog(this, "获取团队信息失败！");
            return;
        }
        
        // 检查权限
        if (!currentUser.getRole().equals("admin") && team.getLeaderId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "您没有权限编辑该团队！");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑团队", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 团队名称
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("团队名称:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField nameField = new JTextField(team.getTeamName(), 20);
        panel.add(nameField, gbc);
        
        // 负责人
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("负责人:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JComboBox<User> leaderCombo = new JComboBox<>();
        List<User> teachers = userDAO.getUsersByRole("teacher");
        for (User teacher : teachers) {
            leaderCombo.addItem(teacher);
            if (teacher.getId() == team.getLeaderId()) {
                leaderCombo.setSelectedItem(teacher);
            }
        }
        panel.add(leaderCombo, gbc);
        
        // 成立日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("成立日期:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField dateField = new JTextField(
            team.getEstablishedDate() != null ? dateFormat.format(team.getEstablishedDate()) : "", 20);
        panel.add(dateField, gbc);
        
        // 研究方向
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("研究方向:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField areaField = new JTextField(team.getResearchArea(), 20);
        panel.add(areaField, gbc);
        
        // 团队描述
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("团队描述:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextArea descArea = new JTextArea(team.getDescription(), 5, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("保存修改");
        saveBtn.addActionListener(e -> {
            team.setTeamName(nameField.getText().trim());
            
            User selectedLeader = (User) leaderCombo.getSelectedItem();
            if (selectedLeader != null) {
                int oldLeaderId = team.getLeaderId();
                team.setLeaderId(selectedLeader.getId());
                
                // 如果负责人变更，更新团队成员
                if (oldLeaderId != selectedLeader.getId()) {
                    teamDAO.removeTeamMember(team.getId(), oldLeaderId);
                    teamDAO.addTeamMember(team.getId(), selectedLeader.getId(), "负责人");
                }
            }
            
            try {
                if (!dateField.getText().trim().isEmpty()) {
                    team.setEstablishedDate(dateFormat.parse(dateField.getText().trim()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                return;
            }
            
            team.setResearchArea(areaField.getText().trim());
            team.setDescription(descArea.getText().trim());
            
            if (teamDAO.updateTeam(team)) {
                JOptionPane.showMessageDialog(dialog, "团队信息更新成功！");
                dialog.dispose();
                loadTeamData();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新失败，请重试！");
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
    
    private void deleteTeam() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个团队！");
            return;
        }
        
        int teamId = (int) tableModel.getValueAt(selectedRow, 0);
        Team team = teamDAO.getTeamById(teamId);
        
        // 检查权限
        if (!currentUser.getRole().equals("admin") && team.getLeaderId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "您没有权限解散该团队！");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要解散该团队吗？\n所有团队成员将被移除。", "确认解散", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (teamDAO.deleteTeam(teamId)) {
                JOptionPane.showMessageDialog(this, "团队已解散！");
                loadTeamData();
            } else {
                JOptionPane.showMessageDialog(this, "解散失败，请重试！");
            }
        }
    }
    
    private void showTeamMembers() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个团队！");
            return;
        }
        
        int teamId = (int) tableModel.getValueAt(selectedRow, 0);
        String teamName = (String) tableModel.getValueAt(selectedRow, 1);
        Team team = teamDAO.getTeamById(teamId);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "团队成员 - " + teamName, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 成员列表
        String[] columns = {"姓名", "角色", "职称", "加入日期"};
        DefaultTableModel memberModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable memberTable = new JTable(memberModel);
        memberTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        List<User> members = teamDAO.getTeamMembers(teamId);
        for (User member : members) {
            Object[] row = {
                member.getRealName(),
                member.getRoleDisplayName(),
                member.getTitle(),
                "2024-01-01"  // 这里应该从team_members表获取实际加入日期
            };
            memberModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // 只有管理员或团队负责人可以添加成员
        if (currentUser.getRole().equals("admin") || team.getLeaderId() == currentUser.getId()) {
            JButton addMemberBtn = new JButton("添加成员");
            addMemberBtn.addActionListener(e -> showAddMemberDialog(teamId));
            buttonPanel.add(addMemberBtn);
            
            JButton removeMemberBtn = new JButton("移除成员");
            removeMemberBtn.addActionListener(e -> {
                int memberRow = memberTable.getSelectedRow();
                if (memberRow != -1) {
                    String memberName = (String) memberModel.getValueAt(memberRow, 0);
                    // 根据姓名查找用户ID
                    for (User member : members) {
                        if (member.getRealName().equals(memberName)) {
                            teamDAO.removeTeamMember(teamId, member.getId());
                            JOptionPane.showMessageDialog(dialog, "成员已移除");
                            dialog.dispose();
                            showTeamMembers(); // 刷新对话框
                            break;
                        }
                    }
                }
            });
            buttonPanel.add(removeMemberBtn);
        }
        
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddMemberDialog(int teamId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加团队成员", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 选择成员
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("选择成员:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<User> memberCombo = new JComboBox<>();
        List<User> allUsers = userDAO.getAllUsers();
        List<User> currentMembers = teamDAO.getTeamMembers(teamId);
        
        for (User user : allUsers) {
            // 过滤掉已经是团队成员的
            boolean isMember = false;
            for (User member : currentMembers) {
                if (member.getId() == user.getId()) {
                    isMember = true;
                    break;
                }
            }
            if (!isMember) {
                memberCombo.addItem(user);
            }
        }
        panel.add(memberCombo, gbc);
        
        // 职位
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("职位:"), gbc);
        
        gbc.gridx = 1;
        JTextField positionField = new JTextField(15);
        panel.add(positionField, gbc);
        
        // 按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton addBtn = new JButton("添加");
        addBtn.addActionListener(e -> {
            User selectedUser = (User) memberCombo.getSelectedItem();
            String position = positionField.getText().trim();
            
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(dialog, "请选择成员！");
                return;
            }
            
            if (position.isEmpty()) {
                position = "成员";
            }
            
            if (teamDAO.addTeamMember(teamId, selectedUser.getId(), position)) {
                JOptionPane.showMessageDialog(dialog, "成员添加成功！");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！");
            }
        });
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(addBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}