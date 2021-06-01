package com.example.demo.extend.diffrentAlgorithm;

import com.example.demo.pojo.EvaluationAlgorithm;
import com.example.demo.service.impl.EvaluationAlgServiceImpl;
import com.example.demo.utils.HanlpProcess;
import com.example.demo.utils.MyTFIDF;
import com.example.demo.utils.ProcessFile;
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
import java.text.DecimalFormat;
import java.util.*;

/**
 * @ClassName LogisticRegressionService
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/13 9:49
 */

@Service
public class LogisticRegressionService {

    //the path of index file
    //fowling two paths need to be modified if run on linux
    public static final String FULL_PATH = "E:\\FinalProject\\datasets\\trec06c\\full\\index_train";
    public static final String FULL_PATH_PRE = "E:\\FinalProject\\datasets\\trec06c\\full\\index_pre";
    public static final String DATA_PRE_PATH = "E:\\FinalProject\\datasets\\trec06c";
    public static final String MODEL_PATH = "E:\\FinalProject\\models\\AlgorithmModels\\LogisticRegression";
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
    public static final Integer FEATURE_NUM = 200;

    @Autowired
    EvaluationAlgServiceImpl evaluationAlgService;

    @Autowired
    JavaSparkContext javaSparkContext;

    public void trainAndPre() {

        ArrayList<String> spamMailList = new ArrayList<>();
        ArrayList<String> hamMailList = new ArrayList<>();

        /*****************read the type and path of all emails*******************************/
        Map<String, String> typeAndPathMap = ProcessFile.getTypeAndPath(FULL_PATH);

        /**********************read spam as will as ham and put them into spamMailList and hanMailList*****************/
        Integer spamNum = 0;
        Integer hamNum = 0;
        int k = 1;
        for (Map.Entry<String, String> entry : typeAndPathMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String path = null;
            //put spam into spamMailList
            if (value.equals("spam") && (spamNum++) < SPAM_NUM_TRAIN) {
                System.out.println("正在读取第" + (k++) + "封邮件：" + key + "\t该邮件为：" + value);
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                spamMailList.add(mail);
            }
            if (value.equals("ham") && (hamNum++) < HAM_NUM_TRAIN) {
                System.out.println("正在读取第" + (k++) + "封邮件：" + key + "\t该邮件为：" + value);
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                hamMailList.add(mail);
            }
            if (hamNum > HAM_NUM_PREDICT && spamNum > SPAM_NUM_PREDICT) {
                break;
            }
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
        List<String> spamTopN = getTopN(keySpamWords, FEATURE_NUM/2);
        List<String> hamTopN = getTopN(keyHamWords, FEATURE_NUM/2);

        // 合并TOPN数据得到特征数据TOP2N
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

        //对topN数据进行本地持久化
        System.out.println("**********************持久化TOP2N********************");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(TOP2N_PATH);
            printWriter.write(keyWordStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
        }

        System.out.println("**********************生成训练数据********************");
        ArrayList<LabeledPoint> spamLabeledPointList = getTrainData(keySpamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList = getTrainData(keyHamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList.addAll(hamLabeledPointList);
        // 将训练数据转化为rdd
        RDD<LabeledPoint> trainRDD = javaSparkContext.parallelize(spamLabeledPointList).rdd();
        // 数据训练生成模型
        System.out.println("**********************训练模型********************");
        LogisticRegressionWithLBFGS logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS();
        LogisticRegressionModel model = logisticRegressionWithLBFGS.setNumClasses(2).run(trainRDD);
        System.out.println("**********************持久化模型********************");
        //源目录不能存在文件，否则保存错误，先删除，再保存
        boolean b = FileUtils.deleteQuietly(new File(MODEL_PATH));
        model.save(javaSparkContext.sc(), MODEL_PATH);

        //进行预测预评估
        //获取垃圾邮件和正常邮件，并放入列表中
        /*****************read the type and path of all emails*******************************/
        Map<String, String> typeAndPathMap_pre = ProcessFile.getTypeAndPath(FULL_PATH_PRE);
        /**********************read spam as will as ham and put them into spamMailList and hanMailList*****************/
        Integer spamNum_pre = 0;
        Integer hamNum_pre = 0;
        int i_pre = 0;
        int j_pre = 0;
        int k_pre = 1;
        ArrayList<String> spamMailList_pre = new ArrayList<>();
        ArrayList<String> hamMailList_pre = new ArrayList<>();
        String[] spamNameList = new String[SPAM_NUM_PREDICT];
        String[] hamNameList = new String[HAM_NUM_PREDICT];
        String[] fileNameList = new String[SPAM_NUM_PREDICT + HAM_NUM_PREDICT];
        for (Map.Entry<String, String> entry : typeAndPathMap_pre.entrySet()) {
            //key是文件相对路径
            String key = entry.getKey();
            //value是文件的类型
            String value = entry.getValue();
            String path = null;
            //put spam into spamMailList
            if (value.equals("spam") && spamNum_pre < SPAM_NUM_PREDICT) {
                spamNum_pre++;
                System.out.println("正在读取第" + (k_pre++) + "封邮件：" + key + "\t该邮件为：" + value);
                spamNameList[i_pre++] = key;
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                spamMailList_pre.add(mail);
            }
            if (value.equals("ham") && hamNum_pre < HAM_NUM_PREDICT) {
                hamNum_pre++;
                System.out.println("正在读取第" + (k_pre++) + "封邮件：" + key + "\t该邮件为：" + value);
                hamNameList[j_pre++] = key;
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                hamMailList_pre.add(mail);
            }
            if (hamNum_pre > HAM_NUM_PREDICT && spamNum_pre > SPAM_NUM_PREDICT) {
                break;
            }
        }
        System.arraycopy(spamNameList, 0, fileNameList, 0, spamNameList.length);
        System.arraycopy(hamNameList, 0, fileNameList, spamNameList.length, hamNameList.length);


        System.out.println("**********************测试集分词********************");
        ArrayList<ArrayList<String>> spamWords = HanlpProcess.cutWords(spamMailList_pre);
        System.out.println(spamWords);
        ArrayList<ArrayList<String>> hamWords = HanlpProcess.cutWords(hamMailList_pre);
        System.out.println(hamWords);
        //对元数据列表进行特征匹配生成训练数据
        System.out.println("**********************生成测试数据********************");
        //1代表垃圾邮件，0代表正常邮件，要和文件对应
        ArrayList<LabeledPoint> spamLabeledPointList_pre = getTrainData(spamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList_pre = getTrainData(hamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList_pre.addAll(hamLabeledPointList_pre);
//        // 将测试数据转化为rdd
//        RDD<LabeledPoint> preRDD = javaSparkContext.parallelize(spamLabeledPointList_pre).rdd();
        //加载持久化的训练模型
        System.out.println("********************正在加载模型************************");
        LogisticRegressionModel logisticRegressionModel = LogisticRegressionModel.load(javaSparkContext.sc(), MODEL_PATH);
        //通过模型对每一封email特征值列表进行预测
        int index = 0;
        int wrong = 0;
        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;
        for (LabeledPoint labeledPoint : spamLabeledPointList_pre) {
            double predictValue = logisticRegressionModel.predict(labeledPoint.features());
            System.out.println("预测文件名为：" + fileNameList[index++]);
            System.out.println("准确值:" + labeledPoint.label() + "\t预测值:" + predictValue);
            if (labeledPoint.label() == 1.0 && predictValue == 1.0) {
                //TP
                TP++;
            } else if (labeledPoint.label() == 1.0 && predictValue == 0.0) {
                FP++;
            } else if (labeledPoint.label() == 0.0 && predictValue == 0.0) {
                TN++;
            } else if (labeledPoint.label() == 0.0 && predictValue == 1.0) {
                FN++;
            }
            String type = "";
            if (labeledPoint.label() == 0.0) {
                type = "正常邮件";
            } else {
                type = "垃圾邮件";
            }
            if (labeledPoint.label() == predictValue) {
                System.out.println("该邮件为:" + type + "\t预测准确");
            } else {
                System.out.println("该邮件为:" + type + "\t预测错误");
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
//                "预测错误率为：" + decimalFormat.format((double)wrong / labeledPoints.size()) + "！"
        );
        System.out.println("TP = " + TP);
        System.out.println("FP = " + FP);
        System.out.println("TN = " + TN);
        System.out.println("FN = " + FN);
        System.out.println("准确率 = " + accuracy);
        System.out.println("查准率 = " + precision);
        System.out.println("召回率 = " + recall);
        System.out.println("F1 = " + F1);
        EvaluationAlgorithm evaluationAlgTable = new EvaluationAlgorithm();
        evaluationAlgTable.setId("LogisticRegression");
        evaluationAlgTable.setAccuracy((float) accuracy);
        evaluationAlgTable.setPre((float) precision);
        evaluationAlgTable.setRecall((float) recall);
        evaluationAlgTable.setF1((float) F1);
        evaluationAlgTable.setFn(FN);
        evaluationAlgTable.setFp(FP);
        evaluationAlgTable.setTn(TN);
        evaluationAlgTable.setTp(TP);
        evaluationAlgTable.setHamTag("ham");
        evaluationAlgTable.setSpamTag("spam");
        evaluationAlgTable.setTotalNumHam(HAM_NUM_PREDICT);
        evaluationAlgTable.setTotalNumSpam(SPAM_NUM_PREDICT);
        boolean b2 = evaluationAlgService.saveOrUpdate(evaluationAlgTable);
        System.out.println("==========程序结束============");

    }

    /*****************get TopN keywords******************************/
    public static List<String> getTopN(ArrayList<ArrayList<String>> keywordList, Integer featureNum) {
        //这个只是单纯的词频统计
        Map<String, Float> tf = MyTFIDF.tfCalculate(keywordList);
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(tf.entrySet());
        //排序算法，对统计出来的词频进行降序排序
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        System.out.println(list);
        List<String> listTopN = new ArrayList<>();
        //选N个TF最大的出来
        for (Map.Entry<String, Float> m : list) {
            listTopN.add(m.getKey());
            if (listTopN.size() == featureNum) {
                break;
            }
        }
        return listTopN;
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
            double[] mapTrain = new double[2 * featureNum];
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

    private static ArrayList<double[]> getCharacter(ArrayList<String> strList, List<String> keyList, Integer featureNum) {
        //实例化装载容器
        ArrayList<double[]> characterNumList = new ArrayList<double[]>();
        //对每一条源数据进行分词对比，生成特征值数组，然后装载进列表容器
        ArrayList<ArrayList<String>> cutList = HanlpProcess.cutWords(strList);
        int i = 0;
        for (ArrayList<String> eachMail : cutList) {
            System.out.println("********正在对第" + (i++) + "封邮件生成特征值列表*********");
            System.out.println(eachMail);
            double[] keyNum = new double[2 * featureNum];
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
