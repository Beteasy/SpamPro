package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import java.util.List;


public interface EvaluationDatasetService extends IMppService<EvaluationDataset> {
    List<EvaluationDataset> getEvaluationDataset();

    void trainAndPreDataTREC();

    void trainAndPreDataSMS();

    void trainAndPreDataSMSTFIDFLR();

    void trainAndPreDataSMSTFIDFNB();

    void trainAndPreDataSMSTFNB();

    void trainAndPreDataSMSTFIDFSVMSGD();

    void trainAndPreDataSMSTFIDFNBLR();
}
