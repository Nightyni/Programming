package com.codebox.controller;

import com.codebox.entity.User;
import com.codebox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 跳转登录页
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 处理登录
    @PostMapping("/login")
    public String login(String username, String password,
                        HttpSession session, Model model) {
        // 验证非空
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "用户名和密码不能为空");
            return "login";
        }

        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/snippet/list";
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    // 跳转注册页
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 处理注册
    @PostMapping("/register")
    public String register(User user, Model model) {
        // 验证非空
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "用户名不能为空");
            return "register";
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            model.addAttribute("error", "密码长度至少6位");
            return "register";
        }
        if (user.getEmail() == null || !user.getEmail().matches("\\w+@\\w+\\.\\w+")) {
            model.addAttribute("error", "邮箱格式不正确");
            return "register";
        }

        if (userService.register(user)) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
        return "redirect:/login";
    }
}