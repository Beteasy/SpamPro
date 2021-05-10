package com.example.demo.controller;


import com.example.demo.service.impl.EvaluationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName DashboardController
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/22 16:14
 */

@Controller
public class DashboardController {
    @Autowired
    EvaluationServiceImpl evaluationService;

    @GetMapping(value = "/dashboard-1")
    public String dashBoard1(){
        return "dashboard/dashboard-1";
    }

}
