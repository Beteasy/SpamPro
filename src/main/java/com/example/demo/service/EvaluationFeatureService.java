package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.EvaluationFeature;

import java.util.List;


public interface EvaluationFeatureService extends IService<EvaluationFeature> {
    List<EvaluationFeature> getEvaluationFeature();
}
