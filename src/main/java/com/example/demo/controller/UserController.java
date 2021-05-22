package com.example.demo.controller;

import com.example.demo.pojo.User;
import com.example.demo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/20 14:20
 */

@Controller
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping(value = "/signup")
    public String signup(User user) {
        int result = userService.addUser(user);
        return "redirect:/login";
    }

    //用户登录
    @PostMapping(value = "/login")
    public String toMailCheck(User user, HttpSession session, Model model) {
        //拿数据去数据库对比
        int result = userService.userLogin(user);
        if (result == 1) {
            session.setAttribute("user", user);
            return "redirect:/mailbox-compose";
        } else {
//            model.addAttribute("msg","用户名或密码错误");
            session.setAttribute("msg", "用户名或密码错误");
            return "redirect:/login";
        }
    }
}
