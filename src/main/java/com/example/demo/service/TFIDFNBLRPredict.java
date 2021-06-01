package com.example.demo.service;

import com.example.demo.utils.HanlpProcess;
import com.example.demo.utils.MyLogisticRegressionModel;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

/**
 * @ClassName TFIDFNBLRPredict
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/30 13:55
 */

@Service
public class TFIDFNBLRPredict {
    public static final String FULL_PATH = "E:\\FinalProject\\datasets\\trec06c\\full\\index_train";
    public static final String FULL_PATH_PRE = "E:\\FinalProject\\datasets\\trec06c\\full\\index_pre";
    public static final String DATA_PRE_PATH = "E:\\FinalProject\\datasets\\trec06c";
    public static final String MODEL_PATH_NB = "E:\\FinalProject\\models\\AlgorithmModels\\TFIDFNBLR\\NB";
    public static final String MODEL_PATH_LR = "E:\\FinalProject\\models\\AlgorithmModels\\TFIDFNBLR\\LR";
    public static final String TOP2N_PATH = "E:\\FinalProject\\datasets\\trec06c\\spam_java.txt";
    //the number of spam used for training
    public static final Integer SPAM_NUM_TRAIN = 3000;
    //the number of ham used for training
    public static final Integer HAM_NUM_TRAIN = 3000;
    //the number of spam used for predicting
    public static final Integer SPAM_NUM_PREDICT = 3000;
    //the number of ham used for predicting
    public static final Integer HAM_NUM_PREDICT = 3000;
    //特征数量
    public static final Integer FEATURE_NUM = 190;

    @Autowired
    JavaSparkContext javaSparkContext;


    public double checkMail(String str) {
        //获取持久化的Top200字符串
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        ArrayList<String> keyString = new ArrayList<>();
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        File file = null;
        String result = null;
        try {
            file = new File(TOP2N_PATH);
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null) {
                stringBuffer.append(tmp);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        result = stringBuffer.toString();
        List<String> keyList = Arrays.asList(result.split(","));
        ArrayList<String> strList = new ArrayList<>();
        strList.add(str);

        //通过源数据列表与TOP200列表数据对比生成特征值列表
        ArrayList<double[]> strNumList = getCharacter(strList, keyList);

        Vector testVec = Vectors.dense(strNumList.get(0));
        //加载持久化的训练模型
        NaiveBayesModel naiveBayesModel = NaiveBayesModel.load(javaSparkContext.sc(), MODEL_PATH_NB);
        LogisticRegressionModel logisticRegressionModel = LogisticRegressionModel.load(javaSparkContext.sc(), MODEL_PATH_LR);
        double predict;
        double predictNB = naiveBayesModel.predict(testVec);
        double predictLR = logisticRegressionModel.predict(testVec);
        System.out.println("predictNB=" + predictNB);
        System.out.println("predictLR=" + predictLR);
        if (predictLR == predictNB) {
            predict = predictNB;
        } else {
//                如果两个的预测值不一样，则按照初始预测数值高的进行计算
            Vector vectorNB = naiveBayesModel.predictProbabilities(testVec);
            double rawPreNB = Arrays.stream(vectorNB.toArray()).max().getAsDouble();
            double rawPreLR = MyLogisticRegressionModel.predictPoint(testVec, logisticRegressionModel.weights(), logisticRegressionModel.intercept());
            System.out.println("rawPreNB=" + rawPreNB);
            System.out.println("rawPreLR=" + rawPreLR);
            if (rawPreNB > rawPreLR) {
                //如果贝叶斯的更高，则结果按照贝叶斯的算
                predict = predictNB;
            } else {
                //否则，按照逻辑回归的算
                predict = predictLR;
            }
            System.out.println("predict=" + predict);
        }
        return predict;
    }
    private static ArrayList<double[]> getCharacter (ArrayList < String > strList, List < String > keyList){
        //实例化装载容器
        ArrayList<double[]> characterNumList = new ArrayList<double[]>();
        ArrayList<ArrayList<String>> cutList = HanlpProcess.cutWords(strList);
        int i = 0;
        for (ArrayList<String> eachMail : cutList) {
            System.out.println("********正在对第" + (i++) + "封邮件生成特征值列表*********");
            System.out.println(eachMail);
            double[] keyNum = new double[FEATURE_NUM];
            for (String s : eachMail) {
                if (keyList.contains(s)) {
                    int index = keyList.indexOf(s);
                    keyNum[index] = 1.0;
                }
            }
            characterNumList.add(keyNum);

        }
        return characterNumList;
    }


}
