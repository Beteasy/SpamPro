package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.pojo.EvaluationFeature;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationDatasetMapper extends MppBaseMapper<EvaluationDataset> {
}
