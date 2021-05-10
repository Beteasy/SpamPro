package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.EvaluationFeatureMapper;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.EvaluationFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName EvaluationFeatureServiceImpl
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/10 20:50
 */
@Service
public class EvaluationFeatureServiceImpl extends ServiceImpl<EvaluationFeatureMapper,EvaluationFeature> implements EvaluationFeatureService {
    @Autowired
    EvaluationFeatureMapper evaluationFeatureMapper;

    @Override
    public List<EvaluationFeature> getEvaluationFeature() {
        List<EvaluationFeature> evaluationFeatures = evaluationFeatureMapper.selectList(null);
        return evaluationFeatures;
    }
}
