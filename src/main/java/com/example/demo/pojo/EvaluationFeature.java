package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Evaluation
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/21 19:05
 */
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class EvaluationFeature {
    private  Integer id;
//    private Integer featureNum;
    private Float accuracy;
    private Float pre;
    private Float recall;
    private Float f1;

    public EvaluationFeature() {
    }

    public EvaluationFeature(Integer id, Float accuracy, Float pre, Float recall, Float f1) {
        this.id = id;
        this.accuracy = accuracy;
        this.pre = pre;
        this.recall = recall;
        this.f1 = f1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
