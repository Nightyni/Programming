package com.example.ui;

import com.example.dao.PaperDAO;
import com.example.dao.ProjectDAO;
import com.example.dao.UserDAO;
import com.example.model.Paper;
import com.example.model.Project;
import com.example.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaperPanel extends JPanel {
    private User currentUser;
    private PaperDAO paperDAO;
    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    
    private JTable paperTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> levelFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PaperPanel(User user) {
        this.currentUser = user;
        this.paperDAO = new PaperDAO();
        this.userDAO = new UserDAO();
        this.projectDAO = new ProjectDAO();
        initializeUI();
        loadPaperData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 创建顶部工具栏
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // 创建表格
        createPaperTable();
        
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
        searchField.addActionListener(e -> searchPapers());
        toolbar.add(searchField);
        
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchPapers());
        toolbar.add(searchButton);
        
        // 级别筛选
        toolbar.add(new JLabel("级别:"));
        String[] levels = {"全部", "SCI", "EI", "核心", "一般"};
        levelFilter = new JComboBox<>(levels);
        levelFilter.addActionListener(e -> filterPapers());
        toolbar.add(levelFilter);
        
        // 刷新按钮
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadPaperData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createPaperTable() {
        // 创建表格模型
        String[] columns = {"ID", "论文标题", "第一作者", "期刊", "发表日期", "级别", "所属项目"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paperTable = new JTable(tableModel);
        paperTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        paperTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        paperTable.setRowHeight(25);
        paperTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        paperTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        paperTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        paperTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paperTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        paperTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        paperTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        paperTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        // 设置级别列渲染器
        paperTable.getColumnModel().getColumn(5).setCellRenderer(new LevelCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(paperTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("论文列表"));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        addButton = new JButton("新增论文");
        editButton = new JButton("编辑论文");
        deleteButton = new JButton("删除论文");
        
        // 根据角色设置按钮可用性
        if (currentUser.getRole().equals("student")) {
            // 学生只能添加和编辑自己的论文，不能删除
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
        addButton.addActionListener(e -> showAddPaperDialog());
        editButton.addActionListener(e -> showEditPaperDialog());
        deleteButton.addActionListener(e -> deletePaper());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        // 添加双击编辑
        paperTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditPaperDialog();
                }
            }
        });
        
        return panel;
    }
    
    private void loadPaperData() {
        tableModel.setRowCount(0);
        
        List<Paper> papers;
        if (currentUser.getRole().equals("admin")) {
            // 管理员看所有论文
            papers = paperDAO.getAllPapers();
        } else {
            // 教师和学生看自己的论文
            papers = paperDAO.getPapersByAuthor(currentUser.getId());
        }
        
        for (Paper paper : papers) {
            Object[] row = {
                paper.getId(),
                paper.getTitle(),
                paper.getAuthorName(),
                paper.getJournal(),
                paper.getPublishDate() != null ? dateFormat.format(paper.getPublishDate()) : "",
                paper.getLevel(),
                paper.getProjectName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchPapers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadPaperData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Paper> papers = paperDAO.getAllPapers();
        
        for (Paper paper : papers) {
            if (paper.getTitle().toLowerCase().contains(keyword) ||
                paper.getAuthorName().toLowerCase().contains(keyword) ||
                paper.getJournal().toLowerCase().contains(keyword)) {
                Object[] row = {
                    paper.getId(),
                    paper.getTitle(),
                    paper.getAuthorName(),
                    paper.getJournal(),
                    paper.getPublishDate() != null ? dateFormat.format(paper.getPublishDate()) : "",
                    paper.getLevel(),
                    paper.getProjectName()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void filterPapers() {
        String level = (String) levelFilter.getSelectedItem();
        if ("全部".equals(level)) {
            loadPaperData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Paper> papers = paperDAO.getPapersByLevel(level);
        
        for (Paper paper : papers) {
            Object[] row = {
                paper.getId(),
                paper.getTitle(),
                paper.getAuthorName(),
                paper.getJournal(),
                paper.getPublishDate() != null ? dateFormat.format(paper.getPublishDate()) : "",
                paper.getLevel(),
                paper.getProjectName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddPaperDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "新增论文", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 论文标题
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("论文标题:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField titleField = new JTextField(30);
        panel.add(titleField, gbc);
        
        // 第一作者（默认为当前用户，教师可改）
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("第一作者:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        if (currentUser.getRole().equals("admin") || currentUser.getRole().equals("teacher")) {
            JComboBox<User> authorCombo = new JComboBox<>();
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                authorCombo.addItem(user);
                if (user.getId() == currentUser.getId()) {
                    authorCombo.setSelectedItem(user);
                }
            }
            panel.add(authorCombo, gbc);
        } else {
            JLabel authorLabel = new JLabel(currentUser.getRealName());
            panel.add(authorLabel, gbc);
        }
        
        // 其他作者
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("其他作者:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField coAuthorsField = new JTextField(30);
        coAuthorsField.setToolTipText("多个作者用逗号分隔");
        panel.add(coAuthorsField, gbc);
        
        // 期刊
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("期刊名称:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField journalField = new JTextField(30);
        panel.add(journalField, gbc);
        
        // 发表日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("发表日期:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JTextField dateField = new JTextField(dateFormat.format(new Date()), 15);
        panel.add(dateField, gbc);
        
        // 卷号
        gbc.gridx = 2;
        panel.add(new JLabel("卷号:"), gbc);
        
        gbc.gridx = 3;
        JTextField volumeField = new JTextField(10);
        panel.add(volumeField, gbc);
        
        // 期号
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("期号:"), gbc);
        
        gbc.gridx = 1;
        JTextField issueField = new JTextField(10);
        panel.add(issueField, gbc);
        
        // 页码
        gbc.gridx = 2;
        panel.add(new JLabel("页码:"), gbc);
        
        gbc.gridx = 3;
        JTextField pagesField = new JTextField(10);
        panel.add(pagesField, gbc);
        
        // DOI
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("DOI:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField doiField = new JTextField(30);
        panel.add(doiField, gbc);
        
        // 级别
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("论文级别:*"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"一般", "核心", "EI", "SCI"});
        panel.add(levelCombo, gbc);
        
        // 所属项目
        gbc.gridx = 2;
        panel.add(new JLabel("所属项目:"), gbc);
        
        gbc.gridx = 3;
        JComboBox<Project> projectCombo = new JComboBox<>();
        projectCombo.addItem(null); // 空选项
        List<Project> projects = projectDAO.getAllProjects();
        for (Project project : projects) {
            projectCombo.addItem(project);
        }
        panel.add(projectCombo, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() || journalField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "论文标题和期刊名称不能为空！");
                return;
            }
            
            Paper paper = new Paper();
            paper.setTitle(titleField.getText().trim());
            
            // 设置作者
            if (currentUser.getRole().equals("admin") || currentUser.getRole().equals("teacher")) {
                JComboBox<User> authorCombo = (JComboBox<User>) panel.getComponent(5);
                User selectedAuthor = (User) authorCombo.getSelectedItem();
                if (selectedAuthor != null) {
                    paper.setAuthorId(selectedAuthor.getId());
                }
            } else {
                paper.setAuthorId(currentUser.getId());
            }
            
            paper.setCoAuthors(coAuthorsField.getText().trim());
            paper.setJournal(journalField.getText().trim());
            
            try {
                if (!dateField.getText().trim().isEmpty()) {
                    paper.setPublishDate(dateFormat.parse(dateField.getText().trim()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                return;
            }
            
            paper.setVolume(volumeField.getText().trim());
            paper.setIssue(issueField.getText().trim());
            paper.setPages(pagesField.getText().trim());
            paper.setDoi(doiField.getText().trim());
            paper.setLevel((String) levelCombo.getSelectedItem());
            
            Project selectedProject = (Project) projectCombo.getSelectedItem();
            if (selectedProject != null) {
                paper.setProjectId(selectedProject.getId());
            }
            
            if (paperDAO.addPaper(paper)) {
                JOptionPane.showMessageDialog(dialog, "论文添加成功！");
                dialog.dispose();
                loadPaperData();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败，请重试！");
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
    
    private void showEditPaperDialog() {
        int selectedRow = paperTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一篇论文！");
            return;
        }
        
        int paperId = (int) tableModel.getValueAt(selectedRow, 0);
        Paper paper = paperDAO.getPaperById(paperId);
        
        if (paper == null) {
            JOptionPane.showMessageDialog(this, "获取论文信息失败！");
            return;
        }
        
        // 检查权限
        if (!currentUser.getRole().equals("admin") && paper.getAuthorId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "您没有权限编辑他人的论文！");
            return;
        }
        
        // 类似新增论文的对话框，但填充现有数据
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑论文", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // 论文标题
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("论文标题:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField titleField = new JTextField(paper.getTitle(), 30);
        panel.add(titleField, gbc);
        
        // 其他作者
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("其他作者:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField coAuthorsField = new JTextField(paper.getCoAuthors(), 30);
        panel.add(coAuthorsField, gbc);
        
        // 期刊
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("期刊名称:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField journalField = new JTextField(paper.getJournal(), 30);
        panel.add(journalField, gbc);
        
        // 发表日期
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("发表日期:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JTextField dateField = new JTextField(
            paper.getPublishDate() != null ? dateFormat.format(paper.getPublishDate()) : "", 15);
        panel.add(dateField, gbc);
        
        // 卷号
        gbc.gridx = 2;
        panel.add(new JLabel("卷号:"), gbc);
        
        gbc.gridx = 3;
        JTextField volumeField = new JTextField(paper.getVolume(), 10);
        panel.add(volumeField, gbc);
        
        // 期号
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("期号:"), gbc);
        
        gbc.gridx = 1;
        JTextField issueField = new JTextField(paper.getIssue(), 10);
        panel.add(issueField, gbc);
        
        // 页码
        gbc.gridx = 2;
        panel.add(new JLabel("页码:"), gbc);
        
        gbc.gridx = 3;
        JTextField pagesField = new JTextField(paper.getPages(), 10);
        panel.add(pagesField, gbc);
        
        // DOI
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("DOI:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField doiField = new JTextField(paper.getDoi(), 30);
        panel.add(doiField, gbc);
        
        // 级别
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel("论文级别:*"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"一般", "核心", "EI", "SCI"});
        levelCombo.setSelectedItem(paper.getLevel());
        panel.add(levelCombo, gbc);
        
        // 所属项目
        gbc.gridx = 2;
        panel.add(new JLabel("所属项目:"), gbc);
        
        gbc.gridx = 3;
        JComboBox<Project> projectCombo = new JComboBox<>();
        projectCombo.addItem(null);
        List<Project> projects = projectDAO.getAllProjects();
        for (Project project : projects) {
            projectCombo.addItem(project);
            if (project.getId() == paper.getProjectId()) {
                projectCombo.setSelectedItem(project);
            }
        }
        panel.add(projectCombo, gbc);
        
        // 按钮
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            paper.setTitle(titleField.getText().trim());
            paper.setCoAuthors(coAuthorsField.getText().trim());
            paper.setJournal(journalField.getText().trim());
            
            try {
                if (!dateField.getText().trim().isEmpty()) {
                    paper.setPublishDate(dateFormat.parse(dateField.getText().trim()));
                } else {
                    paper.setPublishDate(null);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式错误，请使用 yyyy-MM-dd 格式");
                return;
            }
            
            paper.setVolume(volumeField.getText().trim());
            paper.setIssue(issueField.getText().trim());
            paper.setPages(pagesField.getText().trim());
            paper.setDoi(doiField.getText().trim());
            paper.setLevel((String) levelCombo.getSelectedItem());
            
            Project selectedProject = (Project) projectCombo.getSelectedItem();
            if (selectedProject != null) {
                paper.setProjectId(selectedProject.getId());
            } else {
                paper.setProjectId(0);
            }
            
            if (paperDAO.updatePaper(paper)) {
                JOptionPane.showMessageDialog(dialog, "论文更新成功！");
                dialog.dispose();
                loadPaperData();
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
    
    private void deletePaper() {
        int selectedRow = paperTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一篇论文！");
            return;
        }
        
        int paperId = (int) tableModel.getValueAt(selectedRow, 0);
        Paper paper = paperDAO.getPaperById(paperId);
        
        // 检查权限
        if (!currentUser.getRole().equals("admin") && paper.getAuthorId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "您没有权限删除他人的论文！");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除该论文吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (paperDAO.deletePaper(paperId)) {
                JOptionPane.showMessageDialog(this, "论文删除成功！");
                loadPaperData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请重试！");
            }
        }
    }
    
    // 级别列渲染器
    class LevelCellRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        public LevelCellRenderer() {
            setOpaque(true);
            setFont(new Font("微软雅黑", Font.BOLD, 12));
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText(value != null ? value.toString() : "");
            
            if (!isSelected) {
                String level = value != null ? value.toString() : "";
                switch (level) {
                    case "SCI":
                        setBackground(new Color(220, 53, 69, 50));
                        setForeground(new Color(180, 0, 0));
                        break;
                    case "EI":
                        setBackground(new Color(255, 193, 7, 50));
                        setForeground(new Color(150, 100, 0));
                        break;
                    case "核心":
                        setBackground(new Color(40, 167, 69, 50));
                        setForeground(new Color(0, 100, 0));
                        break;
                    case "一般":
                        setBackground(new Color(108, 117, 125, 50));
                        setForeground(new Color(70, 70, 70));
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