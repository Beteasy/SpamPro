package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.CheckRecord;
import org.springframework.stereotype.Repository;


@Repository
public interface MailCheckMapper extends BaseMapper<CheckRecord> {
}
