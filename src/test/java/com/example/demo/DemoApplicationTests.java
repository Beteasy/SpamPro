package com.example.demo;

import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.pojo.Evaluation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private EvaluationMapper evaluationMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void evaluationMapperTest(){
        List<Evaluation> evaluations = evaluationMapper.selectList(null);
        evaluations.forEach(System.out::println);
    }

}