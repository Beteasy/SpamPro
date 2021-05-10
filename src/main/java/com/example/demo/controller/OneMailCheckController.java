package com.example.demo.controller;

import com.example.demo.service.SpamPredict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OneMailCheckController
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/14 14:05
 */
@Controller
public class OneMailCheckController {
    @Autowired
    SpamPredict spamPredict;


    @GetMapping(value = {"/mailbox-compose","/"})
    public String oneMailCheckPage(){
        return "mailbox/mailbox-compose";
    }



    @PostMapping(value = "/docheck")
    @ResponseBody
    public double checkMail(@RequestParam("mailContent") String mailContent){
        double result = spamPredict.textCheck(mailContent);
        System.out.println("检测结果为："+result);
        return result;
    }
}
