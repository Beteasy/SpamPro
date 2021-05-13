package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.EvaluationDatasetMapper;
import com.example.demo.mapper.EvaluationFeatureMapper;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.EvaluationDatasetService;
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
public class EvaluationDatasetServiceImpl extends ServiceImpl<EvaluationDatasetMapper, EvaluationDataset> implements EvaluationDatasetService {
    @Autowired
    EvaluationDatasetMapper evaluationDatasetMapper;


    @Override
    public List<EvaluationDataset> getEvaluationDataset() {
        List<EvaluationDataset> evaluationDatasets = evaluationDatasetMapper.selectList(null);
        return evaluationDatasets;
    }
}
