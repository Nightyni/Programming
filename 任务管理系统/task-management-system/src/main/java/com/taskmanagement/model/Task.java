package com.taskmanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private int taskId;
    private int userId;
    private String title;
    private String description;
    private String status; // 待完成、进行中、已完成、已取消
    private String priority; // 低、中、高
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造方法
    public Task() {}
    
    public Task(int userId, String title, String description, String priority, LocalDate dueDate) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = "待完成";
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    // Getter和Setter
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { 
    this.updatedAt = updatedAt;  // 修正为正确的字段名
}
    
    @Override
    public String toString() {
        return String.format("任务ID: %d | 标题: %s | 状态: %s | 优先级: %s | 截止日期: %s%n描述: %s",
        taskId, title, status, priority, dueDate, description);
    }
}