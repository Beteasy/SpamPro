package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName EchartsController
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/22 20:52
 */
@Controller
public class EchartsController {
    @GetMapping("/echart")
    public String toChartHTML() {
        return "charttest";
    }
}
