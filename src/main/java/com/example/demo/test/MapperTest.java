package com.example.demo.test;

import com.example.demo.mapper.EvaluationFeatureMapper;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.EvaluationFeature;
import com.example.demo.service.impl.EvaluationFeatureServiceImpl;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName MapperTest
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/10 20:42
 */
@MapperScan(basePackages = {"com.example.demo.mapper"})
//@MapperScan("com.example.demo.mapper")
public class MapperTest {
    @Autowired
    EvaluationFeatureMapper evaluationFeatureMapper;

    @Autowired
    EvaluationMapper evaluationMapper;

    @Autowired
    EvaluationFeatureServiceImpl service;

    @Test
    void selectEFMapper(){
        List<EvaluationFeature> evaluationFeatures = service.getEvaluationFeature();
        evaluationFeatures.forEach(System.out::println);
    }

    @Test
    void saveOrUpdateMapper(){
        EvaluationFeature evaluationFeature = new EvaluationFeature();
        evaluationFeature.setId(100);
        evaluationFeature.setAccuracy((float)12.0);
        evaluationFeature.setPre((float)12.0);
        evaluationFeature.setRecall((float)12.0);
        evaluationFeature.setF1((float)12.0);
        //将结果写到数据库
        EvaluationFeatureServiceImpl service = new EvaluationFeatureServiceImpl();
        service.saveOrUpdate(evaluationFeature);
    }

    @Test
    void selectEMapper(){
        List<Evaluation> evaluations = evaluationMapper.selectList(null);
        evaluations.forEach(System.out::println);
    }
}
