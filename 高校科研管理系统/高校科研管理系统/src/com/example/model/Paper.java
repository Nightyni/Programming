package com.example.model;

import java.util.Date;

public class Paper {
    private int id;
    private String title;
    private int authorId;
    private String authorName;  // 关联查询用
    private String coAuthors;    // 其他作者，用逗号分隔或JSON
    private String journal;
    private Date publishDate;
    private String volume;
    private String issue;
    private String pages;
    private String doi;
    private String level;  // SCI, EI, 核心, 一般
    private int projectId;
    private String projectName;  // 关联查询用
    private Date createdAt;
    
    public Paper() {}
    
    public Paper(String title, int authorId, String journal, String level) {
        this.title = title;
        this.authorId = authorId;
        this.journal = journal;
        this.level = level;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getCoAuthors() { return coAuthors; }
    public void setCoAuthors(String coAuthors) { this.coAuthors = coAuthors; }
    
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    
    public Date getPublishDate() { return publishDate; }
    public void setPublishDate(Date publishDate) { this.publishDate = publishDate; }
    
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
    
    public String getPages() { return pages; }
    public void setPages(String pages) { this.pages = pages; }
    
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // 获取作者完整信息
    public String getFullAuthors() {
        if (coAuthors == null || coAuthors.trim().isEmpty()) {
            return authorName;
        } else {
            return authorName + ", " + coAuthors;
        }
    }
    
    // 获取级别颜色
    public String getLevelColor() {
        switch(level) {
            case "SCI": return "red";
            case "EI": return "orange";
            case "核心": return "green";
            case "一般": return "blue";
            default: return "gray";
        }
    }
    
    @Override
    public String toString() {
        return title;
    }
}