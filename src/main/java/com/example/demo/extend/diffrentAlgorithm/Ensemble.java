package com.example.demo.extend.diffrentAlgorithm;

import com.example.demo.service.impl.EvaluationAlgServiceImpl;
import com.example.demo.utils.HanlpProcess;
import com.example.demo.utils.MyTFIDF;
import com.example.demo.utils.ProcessFile;
import com.example.demo.utils.RemoveStopWords;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.spark.ml.classification.StackingClassifier;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @ClassName Ensemble
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/22 16:19
 */

@Service
public class Ensemble {
    //the path of index file
    //fowling paths need to be modified if run on linux
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
    public static final Integer FEATURE_NUM = 200;

    @Autowired
    EvaluationAlgServiceImpl evaluationAlgService;

    @Autowired
    JavaSparkContext javaSparkContext;

    public void trainAndpre(){
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
        }
        System.out.println("**********************正在分词********************");
        ArrayList<ArrayList<String>> spamWordsList = HanlpProcess.cutWords(spamMailList);
        ArrayList<ArrayList<String>> hamWordsList = HanlpProcess.cutWords(hamMailList);

        /*****************remove stop words——新版*******************************/
        System.out.println("**********************正在去除停用词********************");
        ArrayList<ArrayList<String>> keySpamWords = RemoveStopWords.getKeyWordsList(spamWordsList);
        ArrayList<ArrayList<String>> keyHamWords = RemoveStopWords.getKeyWordsList(hamWordsList);

        /********************新增内容：TF-IDF******************************/
        Map<String, Float> hamTF = MyTFIDF.tfCalculate(keyHamWords);
        Map<String, Float> hamTFIDF = MyTFIDF.tfIdfCalculate(hamTF, keyHamWords, HAM_NUM_TRAIN);
        Map<String, Float> spamTF = MyTFIDF.tfCalculate(keySpamWords);
        Map<String, Float> spamTFIDF = MyTFIDF.tfIdfCalculate(spamTF, keySpamWords, SPAM_NUM_TRAIN);
        // 对分词列表进行词频统计获取TOPN数据
        System.out.println("**********************获取TOPN********************");
        /***
         * TF-IDF初始无序
         * 按照value对其进行排序
         * 选取前N/2个特征
         */
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
        //选100个TF-IDF最大的出来
        for(Map.Entry<String,Float> m : hamList){
//            System.out.println(m.getKey()+"="+m.getValue());
            hamTopN.add(m.getKey());
            if (hamTopN.size()==FEATURE_NUM/2){
                break;
            }
        }
        for(Map.Entry<String,Float> m : spamList){
//            System.out.println(m.getKey()+"="+m.getValue());
            spamTopN.add(m.getKey());
            if (spamTopN.size()==FEATURE_NUM/2){
                break;
            }
        }
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

        //对元数据列表进行特征匹配生成训练数据
        System.out.println("**********************对元数据列表进行特征匹配生成训练数据********************");
        ArrayList<LabeledPoint> spamLabeledPointList = getTrainData(keySpamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList = getTrainData(keyHamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList.addAll(hamLabeledPointList);
        // 将训练数据转化为rdd
        RDD<LabeledPoint> trainRDD = javaSparkContext.parallelize(spamLabeledPointList).rdd();
        // 数据训练生成模型
//        new StackingClassifier().setBaseLearners(Array(new NaiveBayes(),new LogisticRegression())).setStacker(new )




    }

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
