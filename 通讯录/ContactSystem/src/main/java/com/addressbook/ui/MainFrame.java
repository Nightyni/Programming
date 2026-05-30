package com.addressbook.ui;

import com.addressbook.model.Contact;
import com.addressbook.service.ContactManager;
import com.addressbook.service.DataExporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame {
    private final ContactManager manager = new ContactManager();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField(20);

    public MainFrame() {
        initUI();
        loadTableData();
    }

    private void initUI() {
        setTitle("通讯录管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 表格设置
        tableModel.setColumnIdentifiers(new String[]{"姓名", "电话", "邮箱"});
        JScrollPane scrollPane = new JScrollPane(table);

        // 功能按钮
        JButton addBtn = new JButton("添加联系人");
        JButton editBtn = new JButton("修改联系人");
        JButton deleteBtn = new JButton("删除联系人");
        JButton exportBtn = new JButton("导出到Excel");
        JButton showAllBtn = new JButton("全部联系人");

        // 搜索面板
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("搜索");
        searchPanel.add(searchBtn);

        // 按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(showAllBtn);

        // 主布局
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // 事件监听
        addBtn.addActionListener(this::handleAdd);
        editBtn.addActionListener(this::handleEdit);
        deleteBtn.addActionListener(this::handleDelete);
        searchBtn.addActionListener(this::handleSearch);
        exportBtn.addActionListener(this::handleExport);
        showAllBtn.addActionListener(this::handleShowAll);
    }

    // 添加联系人处理
    private void handleAdd(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
                "姓名:", nameField,
                "电话:", phoneField,
                "邮箱:", emailField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "添加联系人",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (validateInput(name, phone)) {
                Contact contact = new Contact(name, phone, email);
                if (manager.addContact(contact)) {
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "该手机号已存在！");
                }
            }
        }
    }

    /// 修改联系人处理（带原手机号验证）
    private void handleEdit(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的联系人", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String originalPhone = (String) tableModel.getValueAt(selectedRow, 1);

        // 创建输入字段并填充原始数据
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 0));
        JTextField phoneField = new JTextField(originalPhone);
        JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));

        Object[] message = {
                "姓名:", nameField,
                "电话:", phoneField,
                "邮箱:", emailField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "修改联系人信息",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newEmail = emailField.getText().trim();

            if (validateInput(newName, newPhone)) {
                Contact newContact = new Contact(newName, newPhone, newEmail);
                if (manager.updateContact(originalPhone, newContact)) {
                    loadTableData();
                    JOptionPane.showMessageDialog(this, "修改成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "修改失败：新手机号已存在或原联系人不存在");
                }
            }
        }
    }

    // 删除联系人处理（带确认对话框）
    private void handleDelete(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的联系人", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contactName = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除联系人：" + contactName + " 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String keyword = (String) tableModel.getValueAt(selectedRow, 1); // 使用电话作为唯一标识
            if (manager.deleteContact(keyword)) {
                loadTableData();
                JOptionPane.showMessageDialog(this, "删除成功！");
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请联系人不存在");
            }
        }
    }

    // 搜索联系人处理（支持模糊查询）
    private void handleSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        List<Contact> results = manager.searchContacts(keyword);  // 现在使用java.util.List

        tableModel.setRowCount(0);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配的联系人",
                    "搜索结果", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Contact c : results) {
                tableModel.addRow(new Object[]{c.getName(), c.getPhone(), c.getEmail()});
            }
        }
    }


    // 导出到Excel处理（带文件选择器）
    private void handleExport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存为Excel文件");
        fileChooser.setSelectedFile(new File("contacts.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // 确保文件后缀正确
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try {
                DataExporter.exportToExcel(manager.getAllContacts(), filePath);
                JOptionPane.showMessageDialog(this,
                        "成功导出 " + manager.getAllContacts().size() + " 条记录到：" + filePath,
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "导出失败：" + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //全部联系人
    private void handleShowAll(ActionEvent e) {
        loadTableData(); // 直接调用已有数据加载方法
        searchField.setText(""); // 清空搜索框
    }

    // 加载表格数据
    private void loadTableData() {
        tableModel.setRowCount(0);
        for (Contact c : manager.getAllContacts()) {
            tableModel.addRow(new Object[]{c.getName(), c.getPhone(), c.getEmail()});
        }
    }

    // 输入验证
    private boolean validateInput(String name, String phone) {
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "姓名和电话不能为空");
            return false;
        }
        if (!phone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "电话号码必须是11位数字");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
