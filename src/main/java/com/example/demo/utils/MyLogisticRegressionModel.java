package com.example.demo.utils;

import org.apache.spark.mllib.linalg.Vector;

/**
 * @ClassName MyLogisticRegressionModel
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/31 15:37
 */
public class MyLogisticRegressionModel {

    public static double predictPoint(final Vector dataMatrix, final Vector weightMatrix, final double intercept) {
        double margin = org.apache.spark.mllib.linalg.BLAS.dot(weightMatrix, dataMatrix) + intercept;
        double score = 1.0D / (1.0D + Math.exp(-margin));
        return score;
    }
}
