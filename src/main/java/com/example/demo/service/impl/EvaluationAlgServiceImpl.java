package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.extend.diffrentAlgorithm.*;
import com.example.demo.mapper.EvaluationAlgMapper;
import com.example.demo.pojo.EvaluationAlgorithm;
import com.example.demo.service.EvaluationAlgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName EvaluationAlgServiceImol
 * @Description TODO    针对不同算法的service实现类
 *                      在这里可以对不同算法的模型进行训练和预测，然后将结果写到数据库中去
 *                      在展示结果的时候，也可以处理从数据库中获取结果数据的请求
 * @Author 21971
 * @Date 2021/5/13 9:31
 */

@Service
public class EvaluationAlgServiceImpl extends ServiceImpl<EvaluationAlgMapper, EvaluationAlgorithm> implements EvaluationAlgService {
    @Autowired
    LogisticRegressionService logisticRegressionService;
    @Autowired
    TFIDFNBService tfidfnbService;
    @Autowired
    TFNBService tfnbService;
    @Autowired
    TFIDFWeight tfidfWeight;
    @Autowired
    TFIDFNBLRService tfidfnblrService;
    @Autowired
    EvaluationAlgMapper evaluationAlgMapper;
    @Autowired
    TFIDFSVMSGD tfIdfSVMSGDServ;

    @Override
    public void trainAndPreTFNB() {
        tfnbService.trainAndPre();
    }

    @Override
    public void trainAndPreTFIDFNB() {
        tfidfnbService.trainAndPre();
    }

    @Override
    public void trainAndPreLR() {
        logisticRegressionService.trainAndPre();
    }

    @Override
    public void trainAndPreDT() {

    }

    @Override
    public void trainAndPreTFIDFNBWeight() {
        tfidfWeight.trainAndPre();
    }

    @Override
    public void trainAndPreTfIdfNBLR() {
        tfidfnblrService.trainAndPre();
    }

    @Override
    public void trainAndPreTFIDFSVMSGD() {
        tfIdfSVMSGDServ.trainAndPre();
    }

    @Override
    public List<EvaluationAlgorithm> getAlgEvaluations() {
        List<EvaluationAlgorithm> evaluationAlgorithms = evaluationAlgMapper.selectList(null);
        return evaluationAlgorithms;
    }


}
