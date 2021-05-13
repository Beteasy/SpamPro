package com.example.demo.controller;

import com.example.demo.pojo.Evaluation;
import com.example.demo.service.impl.EvaluationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName EvaluationController
 * @Description TODO    从前端接收查看评估指标的请求，从service层拿到数据返回前端
 * @Author 21971
 * @Date 2021/4/22 14:28
 */

@RestController
public class EvaluationController {
    @Autowired
    EvaluationServiceImpl evaluationService;

    @GetMapping(value = "/evaluation")
    public List<Evaluation> getEvaluation(Model model){
        List<Evaluation> evaluations = evaluationService.getEvaluation();
        model.addAttribute("evaluations", evaluations);
        return evaluations;
    }

}
