package com.example.model;

import java.util.Date;

public class Project {
    private int id;
    private String projectName;
    private String projectNumber;
    private int leaderId;
    private String leaderName;  // 关联查询用
    private double budget;
    private Date startDate;
    private Date endDate;
    private String status;  // 进行中, 已结题, 申请中
    private String description;
    private Date createdAt;
    
    public Project() {}
    
    public Project(String projectName, String projectNumber, int leaderId, 
                   Date startDate, Date endDate) {
        this.projectName = projectName;
        this.projectNumber = projectNumber;
        this.leaderId = leaderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "申请中";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getProjectNumber() { return projectNumber; }
    public void setProjectNumber(String projectNumber) { this.projectNumber = projectNumber; }
    
    public int getLeaderId() { return leaderId; }
    public void setLeaderId(int leaderId) { this.leaderId = leaderId; }
    
    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // 获取状态样式（用于界面显示颜色）
    public String getStatusStyle() {
        switch(status) {
            case "进行中": return "进行中";
            case "已结题": return "已结题";
            case "申请中": return "申请中";
            default: return status;
        }
    }
    
    // 获取状态颜色
    public String getStatusColor() {
        switch(status) {
            case "进行中": return "green";
            case "已结题": return "blue";
            case "申请中": return "orange";
            default: return "gray";
        }
    }
    
    @Override
    public String toString() {
        return projectName + " (" + projectNumber + ")";
    }
}