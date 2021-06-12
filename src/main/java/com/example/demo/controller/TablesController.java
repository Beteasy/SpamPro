package com.example.demo.controller;

import com.example.demo.pojo.EvaluationAlgorithm;
import com.example.demo.service.impl.EvaluationAlgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName TablesController
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/12 17:13
 */

@Controller
public class TablesController {

    @Autowired
    EvaluationAlgServiceImpl evaluationAlgService;

    @GetMapping("/tableDiffAlgorithm")
    public String toTableDiffAlgorithm(Model model){
        List<EvaluationAlgorithm> evaluationAlgorithms = evaluationAlgService.getAlgEvaluations();
        model.addAttribute("evaluationAlgorithms",evaluationAlgorithms);
        return "table/tableDiffAlgorithm";
    }

}
