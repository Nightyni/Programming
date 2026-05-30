package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    
    @RequestMapping("/")
    public String index() {
        return "redirect:/index.jsp";
    }
    
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }
    
    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }
}