package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TablesController
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/12 17:13
 */

@Controller
public class TablesController {
    @GetMapping("/tableDiffAlgorithm")
    public String toTableDiffAlgorithm(){
        return "table/tableDiffAlgorithm";
    }
}
