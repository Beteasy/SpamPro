package com.example.demo.entity;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SparkConfig
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/15 17:09
 */
@Configuration
public class SparkConfig {
    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                // 设置模式为本地模式 [*] 为使用本机核数
                .setMaster("local[*]")
                // 设置应用名
                .setAppName("spam");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }
}
