package com.controller;

import com.pojo.UserForm;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 用户注册
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(UserForm user, Model model) {
        if (userService.checkUsername(user.getUsername())) {
            model.addAttribute("error", "用户名已存在，请重新输入！");
            return "register";
        }
        
        boolean success = userService.register(user);
        if (success) {
            model.addAttribute("message", "注册成功，请登录！");
            return "login";
        } else {
            model.addAttribute("error", "注册失败，请重试！");
            return "register";
        }
    }
    
    // 用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        UserForm user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("loginUser", user);
            return "main";
        } else {
            model.addAttribute("error", "用户名或密码错误！");
            return "login";
        }
    }
    
    // 退出登录
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.jsp";
    }
    
    // 跳转到登录页面
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }
    
    // 跳转到注册页面
    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }
}