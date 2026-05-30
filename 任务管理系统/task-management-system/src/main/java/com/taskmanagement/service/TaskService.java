package com.taskmanagement.service;

import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.model.Task;
import java.time.LocalDate;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    
    public TaskService() {
        this.taskDAO = new TaskDAO();
    }
    
    /**
     * 创建任务
     */
    public boolean createTask(int userId, String title, String description, 
                             String priority, LocalDate dueDate) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("任务标题不能为空");  // 改为System.err
            return false;
        }
        
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            System.err.println("截止日期不能早于今天");  // 改为System.err
            return false;
        }
        
        Task task = new Task(userId, title, description, priority, dueDate);
        return taskDAO.addTask(task);
    }
    
    /**
     * 获取用户的所有任务
     */
    public List<Task> getUserTasks(int userId) {
        return taskDAO.getTasksByUserId(userId);
    }
    
    /**
     * 获取用户的任务（按状态）
     */
    public List<Task> getTasksByStatus(int userId, String status) {
        return taskDAO.getTasksByStatus(userId, status);
    }
    
    /**
     * 获取今日任务
     */
    public List<Task> getTodayTasks(int userId) {
        return taskDAO.getTodayTasks(userId);
    }
    
    /**
     * 更新任务状态
     */
    public boolean updateTaskStatus(int taskId, String status) {
        return taskDAO.updateTaskStatus(taskId, status);
    }
    
    /**
     * 删除任务
     */
    public boolean deleteTask(int taskId) {
        return taskDAO.deleteTask(taskId);
    }
    
    /**
     * 获取任务统计信息
     */
    public void displayTaskStats(int userId) {
        List<Task> allTasks = getUserTasks(userId);
        List<Task> pendingTasks = getTasksByStatus(userId, "待完成");
        List<Task> todayTasks = getTodayTasks(userId);
        
        System.out.println("===== 任务统计 =====");
        System.out.println("总任务数: " + allTasks.size());
        System.out.println("待完成: " + pendingTasks.size());
        System.out.println("今日到期: " + todayTasks.size());
        
        if (!todayTasks.isEmpty()) {
            System.out.println("\n今日到期任务提醒:");
            for (Task task : todayTasks) {
                System.out.println("  • " + task.getTitle() + " (" + task.getPriority() + "优先级)");
            }
        }
    }
}