package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.EvaluationAlgorithm;
import org.springframework.ui.Model;

import java.util.List;


public interface EvaluationAlgService extends IService<EvaluationAlgorithm> {

    void trainAndPreTFNB();
    void trainAndPreTFIDFNB();
    void trainAndPreLR();
    void trainAndPreDT();
    void trainAndPreTFIDFNBWeight();
    void trainAndPreTfIdfNBLR();
    void trainAndPreTFIDFSVMSGD();
    List<EvaluationAlgorithm> getAlgEvaluations();

}
