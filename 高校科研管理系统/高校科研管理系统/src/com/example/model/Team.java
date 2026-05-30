package com.example.model;

import java.util.Date;

public class Team {
    private int id;
    private String teamName;
    private int leaderId;
    private String leaderName;  // 关联查询用
    private Date establishedDate;
    private String researchArea;
    private String description;
    private Date createdAt;
    
    public Team() {}
    
    public Team(String teamName, int leaderId, String researchArea) {
        this.teamName = teamName;
        this.leaderId = leaderId;
        this.researchArea = researchArea;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    
    public int getLeaderId() { return leaderId; }
    public void setLeaderId(int leaderId) { this.leaderId = leaderId; }
    
    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    
    public Date getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(Date establishedDate) { this.establishedDate = establishedDate; }
    
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // 获取成立年限
    public int getYearsEstablished() {
        if (establishedDate == null) return 0;
        long diff = new Date().getTime() - establishedDate.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }
    
    @Override
    public String toString() {
        return teamName;
    }
}