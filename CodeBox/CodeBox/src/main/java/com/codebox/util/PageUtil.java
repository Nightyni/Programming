package com.codebox.util;

import java.util.List;

public class PageUtil<T> {
    private int pageNum;      // 当前页码
    private int pageSize;     // 每页条数
    private int totalCount;   // 总记录数
    private int totalPages;   // 总页数
    private List<T> list;     // 当前页数据

    public PageUtil(int pageNum, int pageSize, int totalCount, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.list = list;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }

    // 计算起始索引（用于 MyBatis 手写分页）
    public static int getStartIndex(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }

    // Getter and Setter
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }

    public boolean isHasPrev() { return pageNum > 1; }
    public boolean isHasNext() { return pageNum < totalPages; }
}