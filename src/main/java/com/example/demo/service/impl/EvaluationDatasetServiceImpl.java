package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.extend.diffrentDataset.*;
import com.example.demo.mapper.EvaluationDatasetMapper;
import com.example.demo.mapper.EvaluationFeatureMapper;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.EvaluationDatasetService;
import com.example.demo.service.EvaluationFeatureService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
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
public class EvaluationDatasetServiceImpl extends MppServiceImpl<EvaluationDatasetMapper, EvaluationDataset> implements EvaluationDatasetService {
    @Autowired
    EvaluationDatasetMapper evaluationDatasetMapper;
    @Autowired
    MySpamTrain_TFIDF_SMS SMSService;
    @Autowired
    MySpamTrain_TFIDF_TREC TRECService;
    @Autowired
    TFIDFLR_SMS tfidflrSms;
    @Autowired
    TFIDFNB_SMS tfidfnbSms;
    @Autowired
    TFNB_SMS tfnbSms;
    @Autowired
    TFIDFSVMSGD_SMS tfidfsvmsgdSms;
    @Autowired
    TFIDFNBLR_SMS tfidfnblrSms;

    @Override
    public List<EvaluationDataset> getEvaluationDataset() {
        List<EvaluationDataset> evaluationDatasets = evaluationDatasetMapper.selectList(null);
        return evaluationDatasets;
    }

    @Override
    public void trainAndPreDataTREC() {
        TRECService.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMS() {
        SMSService.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMSTFIDFLR() {
        tfidflrSms.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMSTFIDFNB() {
        tfidfnbSms.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMSTFNB() {
        tfnbSms.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMSTFIDFSVMSGD() {
        tfidfsvmsgdSms.trainAndPre();
    }

    @Override
    public void trainAndPreDataSMSTFIDFNBLR() {
        tfidfnblrSms.trainAndPre();
    }


}
