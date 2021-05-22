package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;

import java.util.List;


public interface EvaluationDatasetService extends IService<EvaluationDataset> {
    List<EvaluationDataset> getEvaluationDataset();

    void trainAndPreDataTREC();

    void trainAndPreDataSMS();
}
