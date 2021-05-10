package com.example.demo.controller;

import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.impl.EvaluationFeatureServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName EvaluationFeatureController
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/10 20:49
 */
@RestController
public class EvaluationFeatureController {
    @Autowired
    EvaluationFeatureServiceImpl evaluationFeatureService;

    @GetMapping(value = "/evaluationfeature")
    public List<EvaluationFeature> getEvaluationFeature(){
        List<EvaluationFeature> evaluationFeatures = evaluationFeatureService.getEvaluationFeature();
        System.out.println(evaluationFeatures);
        EvaluationFeature evaluationFeature = new EvaluationFeature();
        evaluationFeature.setId(100);
        evaluationFeature.setAccuracy((float)12.0);
        evaluationFeature.setPre((float)12.0);
        evaluationFeature.setRecall((float)12.0);
        evaluationFeature.setF1((float)12.0);
        boolean b = evaluationFeatureService.saveOrUpdate(evaluationFeature);
        System.out.println(b);
        return evaluationFeatures;
    }

}
