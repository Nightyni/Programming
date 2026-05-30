package com.codebox.controller;

import com.codebox.entity.Snippet;
import com.codebox.entity.User;
import com.codebox.service.SnippetService;
import com.codebox.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/snippet")
public class SnippetController {

    @Autowired
    private SnippetService snippetService;

    // 预设语言列表（用于前端下拉框）
    private List<String> getLanguageList() {
        return Arrays.asList("Java", "JavaScript", "Python", "SQL", "HTML/CSS", "XML", "Go", "Other");
    }

    // 预设语言列表（用于 Other 判断，排除这些语言）
    private List<String> getPresetLanguages() {
        return Arrays.asList("Java", "JavaScript", "Python", "SQL", "HTML/CSS", "XML", "Go");
    }

    // 列表页（分页 + 多条件搜索）
    @GetMapping("/list")
    public String list(HttpSession session, Model model,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String language,
                       @RequestParam(required = false) String tags) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 处理空字符串
        keyword = (keyword != null && keyword.trim().isEmpty()) ? null : keyword;
        tags = (tags != null && tags.trim().isEmpty()) ? null : tags;
        
        // 处理语言：如果是 "Other"，传递特殊标识
        String actualLanguage = language;
        if (language != null && "Other".equals(language)) {
            actualLanguage = "Other";
        } else if (language != null && language.trim().isEmpty()) {
            actualLanguage = null;
        }

        PageUtil<Snippet> page = snippetService.searchByCondition(
            user.getId(), keyword, actualLanguage, tags, pageNum, pageSize, getPresetLanguages()
        );

        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("language", language);
        model.addAttribute("tags", tags);
        model.addAttribute("languageList", getLanguageList());

        return "snippet_list";
    }

    // 跳转新增页
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("snippet", new Snippet());
        model.addAttribute("languageList", getLanguageList());
        return "snippet_add";
    }

    // 处理新增
    @PostMapping("/add")
    public String add(HttpSession session, Snippet snippet, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 验证
        if (snippet.getTitle() == null || snippet.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "标题不能为空");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_add";
        }
        if (snippet.getContent() == null || snippet.getContent().trim().isEmpty()) {
            model.addAttribute("error", "代码内容不能为空");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_add";
        }
        if (snippet.getLanguage() == null || snippet.getLanguage().trim().isEmpty()) {
            model.addAttribute("error", "请选择编程语言");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_add";
        }

        snippet.setUserId(user.getId());
        if (snippetService.add(snippet)) {
            return "redirect:/snippet/list";
        } else {
            model.addAttribute("error", "添加失败");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_add";
        }
    }

    // 跳转编辑页
    @GetMapping("/edit")
    public String editPage(@RequestParam Integer id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Snippet snippet = snippetService.findById(id, user.getId());
        if (snippet == null) {
            return "redirect:/snippet/list";
        }

        model.addAttribute("snippet", snippet);
        model.addAttribute("languageList", getLanguageList());
        return "snippet_edit";
    }

    // 处理编辑
    @PostMapping("/edit")
    public String edit(HttpSession session, Snippet snippet, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 验证
        if (snippet.getTitle() == null || snippet.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "标题不能为空");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_edit";
        }
        if (snippet.getContent() == null || snippet.getContent().trim().isEmpty()) {
            model.addAttribute("error", "代码内容不能为空");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_edit";
        }

        snippet.setUserId(user.getId());
        if (snippetService.update(snippet)) {
            return "redirect:/snippet/list";
        } else {
            model.addAttribute("error", "修改失败");
            model.addAttribute("languageList", getLanguageList());
            return "snippet_edit";
        }
    }

    // 删除
    @GetMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            snippetService.delete(id, user.getId());
        }
        return "redirect:/snippet/list";
    }

    // 查看详情（同时增加使用次数）
    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Snippet snippet = snippetService.findById(id, user.getId());
        if (snippet == null) {
            return "redirect:/snippet/list";
        }

        // 增加使用次数
        snippetService.incrementUseCount(id);
        snippet.setUseCount(snippet.getUseCount() + 1);

        model.addAttribute("snippet", snippet);
        return "snippet_detail";
    }
}