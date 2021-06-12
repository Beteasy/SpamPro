package com.example.demo.controller;

import com.example.demo.pojo.EvaluationAlgorithm;
import com.example.demo.service.impl.EvaluationAlgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName EvaluationAlgController
 * @Description TODO    针对不同的算法的controller
 *  *                   在这里可以对不同算法的模型进行训练和预测，然后将结果写到数据库中去
 *  *                   在展示结果的时候，也可以处理从数据库中获取结果数据的请求
 * @Author 21971
 * @Date 2021/5/13 9:09
 */

@RestController
public class EvaluationAlgController {

    @Autowired
    EvaluationAlgServiceImpl evaluationAlgService;

    @GetMapping(value = "/tfNb")
    public void trainAndPreTFNB(){
        evaluationAlgService.trainAndPreTFNB();
    }

    @GetMapping(value = "/tfidfnb")
    public void trainAndPreTFIDFNB(){
        evaluationAlgService.trainAndPreTFIDFNB();
    }

        @GetMapping(value = "/logisticRegression")
    public void trainAndPreLR(){
        evaluationAlgService.trainAndPreLR();
    }


    @GetMapping(value = "/tfIdfWeight")
    public void trainAndPreTFIDFNBWeight(){
        evaluationAlgService.trainAndPreTFIDFNBWeight();
    }

    @GetMapping(value = "/tfIdfNBLR")
    public void trainAndPreTfIdfNBLR(){
        evaluationAlgService.trainAndPreTfIdfNBLR();
    }

    @GetMapping(value = "/tfIdfSVMSGD")
    public void trainAndPreTFIDFSVMSGD(){
        evaluationAlgService.trainAndPreTFIDFSVMSGD();
    }



    @GetMapping(value = "/evaluationsAlgBar")
    public List<EvaluationAlgorithm> getAlgEvaluationsBar(){
        List<EvaluationAlgorithm> evaluationAlgorithms = evaluationAlgService.getAlgEvaluations();
        return evaluationAlgorithms;
    }

}
