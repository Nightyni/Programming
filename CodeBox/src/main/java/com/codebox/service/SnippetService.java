package com.codebox.service;

import com.codebox.entity.Snippet;
import com.codebox.util.PageUtil;

import java.util.List;

public interface SnippetService {

    // 多条件搜索 + 分页（支持 Other 语言）
    PageUtil<Snippet> searchByCondition(Integer userId, String keyword,
                                         String language, String tags,
                                         Integer pageNum, Integer pageSize,
                                         List<String> presetLanguages);

    // 根据ID查询
    Snippet findById(Integer id, Integer userId);

    // 新增
    boolean add(Snippet snippet);

    // 修改
    boolean update(Snippet snippet);

    // 删除
    boolean delete(Integer id, Integer userId);

    // 增加使用次数
    void incrementUseCount(Integer id);
}