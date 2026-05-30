package com.codebox.service.impl;

import com.codebox.entity.Snippet;
import com.codebox.mapper.SnippetMapper;
import com.codebox.service.SnippetService;
import com.codebox.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnippetServiceImpl implements SnippetService {

    @Autowired
    private SnippetMapper snippetMapper;

    @Override
    public PageUtil<Snippet> searchByCondition(Integer userId, String keyword,
                                               String language, String tags,
                                               Integer pageNum, Integer pageSize,
                                               List<String> presetLanguages) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据
        List<Snippet> list = snippetMapper.searchByCondition(
            userId, keyword, language, tags, offset, pageSize, presetLanguages
        );

        // 查询总数
        int totalCount = snippetMapper.countByCondition(userId, keyword, language, tags, presetLanguages);

        return new PageUtil<>(pageNum, pageSize, totalCount, list);
    }

    @Override
    public Snippet findById(Integer id, Integer userId) {
        return snippetMapper.findById(id, userId);
    }

    @Override
    public boolean add(Snippet snippet) {
        snippet.setUseCount(0);
        return snippetMapper.insert(snippet) > 0;
    }

    @Override
    public boolean update(Snippet snippet) {
        return snippetMapper.update(snippet) > 0;
    }

    @Override
    public boolean delete(Integer id, Integer userId) {
        return snippetMapper.delete(id, userId) > 0;
    }

    @Override
    public void incrementUseCount(Integer id) {
        snippetMapper.incrementUseCount(id);
    }
}