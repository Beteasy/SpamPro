package com.example.demo.controller;

import com.example.demo.extend.findfeaturenum.MySpamTrain_Frequency_FindFeatureNum;
import com.example.demo.extend.findfeaturenum.MySpamTrain_TFIDFW_FindFeatureNum;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.impl.EvaluationFeatureServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    MySpamTrain_Frequency_FindFeatureNum trainAndPreServ;
    @Autowired
    MySpamTrain_TFIDFW_FindFeatureNum tfIDFWeightFeatureServ;

    @GetMapping(value = "/evaluationfeature")
    public List<EvaluationFeature> getEvaluationFeature(){
        List<EvaluationFeature> evaluationFeatures = evaluationFeatureService.getEvaluationFeature();
        return evaluationFeatures;
    }

    @GetMapping(value = "/trainAndPreFeature")
    public void trainAndPreFeature(){
        trainAndPreServ.trainAndPre();
    }


    @GetMapping(value = "/trainAndPreFeatureTFIDFW")
    public void trainAndPreFeatureTFIDFW(){
        tfIDFWeightFeatureServ.trainAndPre();
    }

}
