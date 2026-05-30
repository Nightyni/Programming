package com.example.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String realName;
    private String role;  // admin, teacher, student
    private String department;
    private String title;  // 职称
    
    public User() {}
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = "student";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    // 获取角色显示名称
    public String getRoleDisplayName() {
        switch(role) {
            case "admin": return "管理员";
            case "teacher": return "教师";
            case "student": return "学生";
            default: return role;
        }
    }
    
    @Override
    public String toString() {
        return realName != null ? realName : username;
    }
}