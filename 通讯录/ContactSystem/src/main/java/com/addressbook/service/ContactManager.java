package com.addressbook.service;

import com.addressbook.model.Contact;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private static final String DATA_FILE = "contacts.dat";
    private List<Contact> contacts = new ArrayList<>();

    public ContactManager() {
        loadData();
    }

    // 添加联系人
    public boolean addContact(Contact contact) {
        if (isPhoneExists(contact.getPhone())) {
            return false;
        }
        contacts.add(contact);
        saveData();
        return true;
    }

    // 删除联系人
    public boolean deleteContact(String keyword) {
        boolean removed = contacts.removeIf(c ->
                c.getName().equalsIgnoreCase(keyword) ||
                        c.getPhone().equals(keyword));
        if (removed) saveData();
        return removed;
    }

    // 修改联系人
    public boolean updateContact(String originalPhone, Contact newContact) {
        for (Contact c : contacts) {
            if (c.getPhone().equals(originalPhone)) {
                if (!originalPhone.equals(newContact.getPhone()) &&
                        isPhoneExists(newContact.getPhone())) {
                    return false;
                }
                c.setName(newContact.getName());
                c.setPhone(newContact.getPhone());
                c.setEmail(newContact.getEmail());
                saveData();
                return true;
            }
        }
        return false;
    }

    // 查询联系人
    public List<Contact> searchContacts(String keyword) {
        List<Contact> result = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getName().contains(keyword) || c.getPhone().contains(keyword)) {
                result.add(c);
            }
        }
        return result;
    }

    // 数据持久化
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            showErrorDialog("数据保存失败: " + e.getMessage());
        }
    }

    // 数据加载
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                contacts = (List<Contact>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                showErrorDialog("数据加载失败: " + e.getMessage());
            }
        }
    }

    private boolean isPhoneExists(String phone) {
        for (Contact c : contacts) {
            if (c.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    private void showErrorDialog(String message) {
        javax.swing.JOptionPane.showMessageDialog(
                null,
                message,
                "系统错误",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
}
