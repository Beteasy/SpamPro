package com.example.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.mapper.EvaluationAlgMapper;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.EvaluationAlgorithm;
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

//    @Test
//    public void testPage(){
//        Page<EvaluationAlgorithm> page = new Page<>(2,5);
//        EvaluationAlgMapper.selectPage(page,null);
//        page.getRecords().forEach(System.out::println);
//        System.out.println(page.getTotal());
//    }


}