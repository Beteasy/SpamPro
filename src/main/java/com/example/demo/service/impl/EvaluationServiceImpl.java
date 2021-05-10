package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.pojo.Evaluation;
import com.example.demo.service.EvaluationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName EvaluationServiceImpl
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/22 14:53
 */

@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    public List<Evaluation> getEvaluation() {
        List<Evaluation> evaluations = evaluationMapper.selectList(null);
        return evaluations;
    }
}
