package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EvaluationDatasetMapper
 * @Description TODO  处理不同数据集的POJO
 * @Author 21971
 * @Date 2021/5/11 16:29
 */

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class EvaluationDataset {
    @MppMultiId
    @TableField(value = "id")
    private String id;  //数据集名称
    @MppMultiId
    @TableField(value = "algorithm")
    private String algorithm;
    private Float accuracy;
    private Float pre;
    private Float recall;
    private Float f1;

    public EvaluationDataset() {
    }

    public EvaluationDataset(String id, String algorithm, Float accuracy, Float pre, Float recall, Float f1) {
        this.id = id;
        this.algorithm = algorithm;
        this.accuracy = accuracy;
        this.pre = pre;
        this.recall = recall;
        this.f1 = f1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getPre() {
        return pre;
    }

    public void setPre(Float pre) {
        this.pre = pre;
    }

    public Float getRecall() {
        return recall;
    }

    public void setRecall(Float recall) {
        this.recall = recall;
    }

    public Float getF1() {
        return f1;
    }

    public void setF1(Float f1) {
        this.f1 = f1;
    }
}
