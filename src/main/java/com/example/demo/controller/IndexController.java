package com.example.demo.controller;

import com.example.demo.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * @ClassName IndexController
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/20 14:23
 */

@Controller
public class IndexController {

    @GetMapping(value = {"/","/login"})
    public String loginPage(HttpSession session){
//        if (session.getAttribute("user") != null){
//            return "/mailbox.html";
//        }else {
//            return "login/signin";
//        }
        return "login/signin";
    }


    @GetMapping(value = "/signup")
    public String toSignupPage(){
        return "login/signup";
    }

    @GetMapping(value = "/mailbox-compose")
    public String toMailCheckPage(HttpSession session, Model model){
        Object user = session.getAttribute("user");
        if (user != null){
            return "mailbox/mailbox-compose";
        }else {
            model.addAttribute("msg","请先登录");
            return "/login";
        }

    }

}
