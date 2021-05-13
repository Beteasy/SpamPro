package com.example.demo.controller;

import com.example.demo.extend.diffrentDataset.MySpamTrain_TFIDF_SMS;
import com.example.demo.extend.findfeaturenum.MySpamTrain_Frequency_FindFeatureNum;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.impl.EvaluationDatasetServiceImpl;
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
public class EvaluationDatasetController {
    @Autowired
    EvaluationDatasetServiceImpl evaluationDatasetService;
    @Autowired
    MySpamTrain_TFIDF_SMS mySpamTrain_tfidf_sms;

    @GetMapping(value = "/evaluationdataset")
    public List<EvaluationDataset> getEvaluationDataset(){
        List<EvaluationDataset> evaluationDatasets = evaluationDatasetService.getEvaluationDataset();
        return evaluationDatasets;
    }

    @GetMapping(value = "/trainAndPreDataset")
    public void trainAndPreDataset(){
        mySpamTrain_tfidf_sms.trainAndPre();
    }

}
