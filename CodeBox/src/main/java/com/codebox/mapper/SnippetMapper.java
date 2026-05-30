package com.codebox.mapper;

import com.codebox.entity.Snippet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnippetMapper {

    // 多条件模糊搜索 + 分页（支持 Other 语言）
    List<Snippet> searchByCondition(@Param("userId") Integer userId,
                                     @Param("keyword") String keyword,
                                     @Param("language") String language,
                                     @Param("tags") String tags,
                                     @Param("offset") Integer offset,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("presetLanguages") List<String> presetLanguages);

    // 统计搜索结果总数（用于分页）
    int countByCondition(@Param("userId") Integer userId,
                         @Param("keyword") String keyword,
                         @Param("language") String language,
                         @Param("tags") String tags,
                         @Param("presetLanguages") List<String> presetLanguages);

    // 根据ID查询
    Snippet findById(@Param("id") Integer id, @Param("userId") Integer userId);

    // 新增
    int insert(Snippet snippet);

    // 修改
    int update(Snippet snippet);

    // 删除
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);

    // 增加使用次数
    int incrementUseCount(@Param("id") Integer id);
}