package com.codebox.entity;

import java.util.Date;

public class Snippet {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String language;
    private String tags;
    private Integer useCount;
    private Date createTime;
    private Date updateTime;

    public Snippet() {}

    public Snippet(Integer userId, String title, String content, String language, String tags) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.language = language;
        this.tags = tags;
        this.useCount = 0;
    }

    // Getter and Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Integer getUseCount() { return useCount; }
    public void setUseCount(Integer useCount) { this.useCount = useCount; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "Snippet{id=" + id + ", title='" + title + "', language='" + language + "'}";
    }
}