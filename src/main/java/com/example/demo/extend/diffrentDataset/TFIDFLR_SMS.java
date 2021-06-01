package com.example.demo.extend.diffrentDataset;

import com.example.demo.pojo.EvaluationDataset;
import com.example.demo.service.EvaluationDatasetService;
import com.example.demo.utils.HanlpProcess;
import com.example.demo.utils.MyTFIDF;
import com.example.demo.utils.RemoveStopWords;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @ClassName Excutor
 * @Description TODO
 * @Author 21971
 * @Date 2021/1/30 15:33
 */

@Service
public class TFIDFLR_SMS {
    //the path of index file
    //fowling two paths need to be modified if run on linux
    public static final String FULL_PATH_TRAIN = "E:\\FinalProject\\datasets\\SMS\\train.txt";
    public static final String FULL_PATH_PRE = "E:\\FinalProject\\datasets\\SMS\\test.txt";
    public static final String MODEL_PATH = "E:\\FinalProject\\models\\DatasetModel\\SMS\\TFIDFLR";
    public static final String TOP2N_PATH = "E:\\FinalProject\\models\\DatasetModel\\top2n.txt";
    //the number of spam used for training
    public static final Integer SPAM_NUM_TRAIN = 3000;
    //the number of ham used for training
    public static final Integer HAM_NUM_TRAIN = 3000;
    //the number of spam used for predicting
    public static final Integer SPAM_NUM_PREDICT = 3000;
    //the number of ham used for predicting
    public static final Integer HAM_NUM_PREDICT = 3000;
    //特征数量
    public static final Integer FEATURE_NUM = 200;


    @Autowired
    JavaSparkContext javaSparkContext;
    @Autowired
    EvaluationDatasetService evaluationDatasetService;

    public void trainAndPre() {

        /**********************read spam as will as ham and put them into spamMailList and hanMailList*****************/
        Integer spamNum = 0;
        Integer hamNum = 0;
        ArrayList<String> spamMailList = new ArrayList<>();
        ArrayList<String> hamMailList = new ArrayList<>();

        StringBuffer str = new StringBuffer();//考虑用这个在分词前替换掉所有的非中文字符
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        File file = null;
        String result = null;
        Integer type;
        String SMSContent;
        try {
            file = new File(FULL_PATH_TRAIN);
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null) {
                //将数据拆开，第一部分为类型，0为正常邮件，1为垃圾邮件
                //第二部分是短信内容
                String[] strings = tmp.split("\t");
                type = Integer.valueOf(strings[0]);
                SMSContent = strings[1];
                if (type == 0 && (hamNum++) < HAM_NUM_TRAIN) {
                    //读取正常邮件
                    System.out.println("正在读取第" + (hamNum) + "封邮件：" + "\t该邮件为：" + SMSContent);
                    hamMailList.add(SMSContent);
                }
                if (type == 1 && (spamNum++) < SPAM_NUM_TRAIN) {
                    //读取垃圾邮件
                    System.out.println("正在读取第" + (spamNum) + "封邮件：" + "\t该邮件为：" + SMSContent);
                    spamMailList.add(SMSContent);
                }
                if (hamNum >= HAM_NUM_TRAIN && spamNum >= SPAM_NUM_TRAIN) {
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("**********************正在分词********************");
        ArrayList<ArrayList<String>> spamWordsList = HanlpProcess.cutWords(spamMailList);
        ArrayList<ArrayList<String>> hamWordsList = HanlpProcess.cutWords(hamMailList);

        /*****************remove stop words——新版*******************************/
        System.out.println("**********************正在去除停用词********************");
        ArrayList<ArrayList<String>> keySpamWords = RemoveStopWords.getKeyWordsList(spamWordsList);
        ArrayList<ArrayList<String>> keyHamWords = RemoveStopWords.getKeyWordsList(hamWordsList);

        // 对分词列表进行词频统计获取TOPN数据
        System.out.println("**********************获取TOPN********************");
        Map<String, Float> hamTF = MyTFIDF.tfCalculate(keyHamWords);
        Map<String, Float> hamTFIDF = MyTFIDF.tfIdfCalculate(hamTF, keyHamWords, HAM_NUM_TRAIN);
        Map<String, Float> spamTF = MyTFIDF.tfCalculate(keySpamWords);
        Map<String, Float> spamTFIDF = MyTFIDF.tfIdfCalculate(spamTF, keySpamWords, SPAM_NUM_TRAIN);
        List<Map.Entry<String, Float>> hamList = new ArrayList<Map.Entry<String, Float>>(hamTFIDF.entrySet());
        Collections.sort(hamList, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<Map.Entry<String, Float>> spamList = new ArrayList<Map.Entry<String, Float>>(spamTFIDF.entrySet());
        Collections.sort(spamList, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<String> spamTopN = new ArrayList<>();
        List<String> hamTopN = new ArrayList<>();
        for(Map.Entry<String,Float> m : hamList){
            hamTopN.add(m.getKey());
            if (hamTopN.size()==FEATURE_NUM/2){
                break;
            }
        }
        for(Map.Entry<String,Float> m : spamList){
            spamTopN.add(m.getKey());
            if (spamTopN.size()==FEATURE_NUM/2){
                break;
            }
        }

        System.out.println("**********************合并得到TOP2N********************");
        ArrayList<String> allKeyWord = new ArrayList<>();
        String keyWordStr = "";
        for (String word : spamTopN) {
            allKeyWord.add(word);
            //每个词之间用空格分离
            keyWordStr += word + ",";
        }
        for (String word : hamTopN) {
            allKeyWord.add(word);
            //每个词之间用空格分离
            keyWordStr += word + ",";
        }

        //对元数据列表进行特征匹配生成训练数据
        System.out.println("**********************对元数据列表进行特征匹配生成训练数据********************");
        //1代表垃圾邮件，0代表正常邮件，要和文件对应
        ArrayList<LabeledPoint> spamLabeledPointList = getTrainData(keySpamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList = getTrainData(keyHamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList.addAll(hamLabeledPointList);
        // 将训练数据转化为rdd
        RDD<LabeledPoint> trainRDD = javaSparkContext.parallelize(spamLabeledPointList).rdd();

        System.out.println("**********************训练模型********************");
        LogisticRegressionWithLBFGS logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS();
        LogisticRegressionModel model = logisticRegressionWithLBFGS.setNumClasses(2).run(trainRDD);
        System.out.println("**********************持久化模型********************");
        //源目录不能存在文件，否则保存错误，先删除，再保存
        boolean b = FileUtils.deleteQuietly(new File(MODEL_PATH));
        model.save(javaSparkContext.sc(), MODEL_PATH);

        System.out.println("**********************开始预测与评估********************");
        /**********************read spam as will as ham and put them into spamMailList and hanMailList*****************/
        Integer spamNum_pre = 0;
        Integer hamNum_pre = 0;
        ArrayList<String> spamMailList_pre = new ArrayList<>();
        ArrayList<String> hamMailList_pre = new ArrayList<>();
        try {
            file = new File(FULL_PATH_PRE);
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null) {
                //将数据拆开，第一部分为类型，0为正常邮件，1为垃圾邮件
                //第二部分是短信内容
                String[] strings = tmp.split("\t");
                type = Integer.valueOf(strings[0]);
                SMSContent = strings[1];
                if (type == 0 && (hamNum_pre++) < HAM_NUM_TRAIN) {
                    //读取正常邮件
                    System.out.println("正在读取第" + (hamNum_pre) + "封邮件：" + "\t该邮件为：" + SMSContent);
                    hamMailList_pre.add(SMSContent);
                }
                if (type == 1 && (spamNum_pre++) < SPAM_NUM_TRAIN) {
                    //读取垃圾邮件
                    System.out.println("正在读取第" + (spamNum_pre) + "封邮件：" + "\t该邮件为：" + SMSContent);
                    spamMailList_pre.add(SMSContent);
                }
                if (hamNum_pre >= HAM_NUM_PREDICT && spamNum_pre >= SPAM_NUM_PREDICT) {
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("**********************测试集分词********************");
        ArrayList<ArrayList<String>> spamWords = HanlpProcess.cutWords(spamMailList_pre);
//        System.out.println(spamWords);
        ArrayList<ArrayList<String>> hamWords = HanlpProcess.cutWords(hamMailList_pre);
//        System.out.println(hamWords);
        //对元数据列表进行特征匹配生成训练数据
        System.out.println("**********************对元数据列表进行特征匹配生成训练数据********************");
        //1代表垃圾邮件，0代表正常邮件，要和文件对应
        ArrayList<LabeledPoint> spamLabeledPointList_pre = getTrainData(spamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList_pre = getTrainData(hamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList_pre.addAll(hamLabeledPointList_pre);
        // 将测试数据转化为rdd
//        RDD<LabeledPoint> preRDD = javaSparkContext.parallelize(spamLabeledPointList_pre).rdd();

        //加载持久化的训练模型
        System.out.println("********************正在加载模型************************");
        //通过模型对每一封email特征值列表进行预测
        int wrong = 0;
        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;
        for (LabeledPoint labeledPoint : spamLabeledPointList_pre) {
            double predictValue = model.predict(labeledPoint.features());
            if (labeledPoint.label() == 1.0 && predictValue == 1.0) {
                TP++;
            } else if (labeledPoint.label() == 1.0 && predictValue == 0.0) {
                FP++;
            } else if (labeledPoint.label() == 0.0 && predictValue == 0.0) {
                TN++;
            } else if (labeledPoint.label() == 0.0 && predictValue == 1.0) {
                FN++;
            }
            String typeStr = "";
            //这里把1.0改成了2.0
            if (labeledPoint.label() == 0.0) {
                typeStr = "正常邮件";
            } else {
                typeStr = "垃圾邮件";
            }
            if (labeledPoint.label() == predictValue) {
                System.out.println("该邮件为:" + typeStr + "\t预测准确");
            } else {
                System.out.println("该邮件为:" + typeStr + "\t预测错误");
                wrong++;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        DecimalFormat F1Format = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy = Double.valueOf(decimalFormat.format((double) (spamLabeledPointList_pre.size() - wrong) / spamLabeledPointList_pre.size()))*100;
        double precision = Double.valueOf(decimalFormat.format(((double) TP / (TP + FP))))*100;
        double recall = Double.valueOf(decimalFormat.format(((double) TP / (TP + FN))))*100;
        double F1 = Double.valueOf(F1Format.format((2 * precision * recall) / (precision + recall)));
        System.out.println("======================");
        System.out.println("预测完毕！一共预测" + spamLabeledPointList_pre.size() + "封邮件！\r\n" +
                "预测准确：" + (spamLabeledPointList_pre.size() - wrong) + "封邮件！\r\n" +
                "预测错误：" + wrong + "封邮件！\r\n" +
                "预测准确率为：" + accuracy + "！\r\n"
        );
        System.out.println("TP = " + TP);
        System.out.println("FP = " + FP);
        System.out.println("TN = " + TN);
        System.out.println("FN = " + FN);
        System.out.println("查准率 = " + precision);
        System.out.println("召回率 = " + recall);
        System.out.println("F1 = " + F1);
        EvaluationDataset evaluationDataset = new EvaluationDataset();
        evaluationDataset.setId("SMS");
        evaluationDataset.setAlgorithm("TFIDFLR");
        evaluationDataset.setAccuracy((float) accuracy);
        evaluationDataset.setPre((float) precision);
        evaluationDataset.setRecall((float) recall);
        evaluationDataset.setF1((float) F1);
        boolean b2 = evaluationDatasetService.saveOrUpdate(evaluationDataset);
        System.out.println("==========程序结束============");


    }

    /**
     * 通过分词列表与关键词列表获取训练映射数据
     *
     * @param
     * @param keyWords
     * @param type
     * @return
     */
    public static ArrayList<LabeledPoint> getTrainData(ArrayList<ArrayList<String>> wordList, ArrayList<String> keyWords, double type, Integer featureNum) {
        // 初始化向量标签容器
        int i = 1;
        ArrayList<LabeledPoint> featureBox = new ArrayList<LabeledPoint>();
        for (ArrayList<String> emailMeta : wordList) {
            System.out.println("********正在对第" + i + "封邮件映射转化*********");
            // 对比映射转化
            double[] mapTrain = new double[featureNum];
            int mapIndex = 0;
            for (String key : keyWords) {
                if (emailMeta.contains(key)) {
                    //这里可以改权重，后期实现加权贝叶斯
                    mapTrain[mapIndex] = 1.0;
                }
                mapIndex++;
            }
            featureBox.add(new LabeledPoint(type, Vectors.dense(mapTrain)));
        }

        return featureBox;
    }

}
