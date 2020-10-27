package com.sxt.controller;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {


    /**
     * Shiro 框架认证失败后跳转控制
     * @return
     */
    @RequestMapping("/loginError")
    public String loginError(HttpServletRequest request, Model model){
        String shiroLoginFailure = (String) request.getAttribute("shiroLoginFailure");

        if(shiroLoginFailure != null){
            if(UnknownAccountException.class.getName().equals(shiroLoginFailure)){
                model.addAttribute("errorMsg", "账号不存在");
            }
            else if(IncorrectCredentialsException.class.getName().equals(shiroLoginFailure)){
                model.addAttribute("errorMsg","密码错误");
            }
        }

        return "forward:/admin/login";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
