package com.example.demo.extend.diffrentAlgorithm;

import com.example.demo.service.impl.EvaluationAlgServiceImpl;
import com.example.demo.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

;

/**
 * @ClassName LogisticRegressionService
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/13 9:49
 */

@Service
public class TFIDFNBLRService {

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
    //????????????
    public static final Integer FEATURE_NUM = 190;

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
                System.out.println("???????????????" + (k++) + "????????????" + key + "\t???????????????" + value);
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                spamMailList.add(mail);
            }
            if (value.equals("ham") && (hamNum++) < HAM_NUM_TRAIN) {
                System.out.println("???????????????" + (k++) + "????????????" + key + "\t???????????????" + value);
                path = key.replace("..", DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                hamMailList.add(mail);
            }
        }
        System.out.println("**********************????????????********************");
        ArrayList<ArrayList<String>> spamWordsList = HanlpProcess.cutWords(spamMailList);
        ArrayList<ArrayList<String>> hamWordsList = HanlpProcess.cutWords(hamMailList);

        /*****************remove stop words????????????*******************************/
        System.out.println("**********************?????????????????????********************");
        ArrayList<ArrayList<String>> keySpamWords = RemoveStopWords.getKeyWordsList(spamWordsList);
        ArrayList<ArrayList<String>> keyHamWords = RemoveStopWords.getKeyWordsList(hamWordsList);

        /********************???????????????TF-IDF******************************/
        Map<String, Float> hamTF = MyTFIDF.tfCalculate(keyHamWords);
        Map<String, Float> hamTFIDF = MyTFIDF.tfIdfCalculate(hamTF, keyHamWords, HAM_NUM_TRAIN);
        Map<String, Float> spamTF = MyTFIDF.tfCalculate(keySpamWords);
        Map<String, Float> spamTFIDF = MyTFIDF.tfIdfCalculate(spamTF, keySpamWords, SPAM_NUM_TRAIN);
        // ???????????????????????????????????????TOPN??????
        System.out.println("**********************??????TOPN********************");
        /***
         * TF-IDF????????????
         * ??????value??????????????????
         * ?????????N/2?????????
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
        //???100???TF-IDF???????????????
        for (Map.Entry<String, Float> m : hamList) {
//            System.out.println(m.getKey()+"="+m.getValue());
            hamTopN.add(m.getKey());
            if (hamTopN.size() == FEATURE_NUM / 2) {
                break;
            }
        }
        for (Map.Entry<String, Float> m : spamList) {
//            System.out.println(m.getKey()+"="+m.getValue());
            spamTopN.add(m.getKey());
            if (spamTopN.size() == FEATURE_NUM / 2) {
                break;
            }
        }
        // ??????TOPN????????????????????????TOP2N
        System.out.println("**********************????????????TOP2N********************");
        ArrayList<String> allKeyWord = new ArrayList<>();
        String keyWordStr = "";
        for (String word : spamTopN) {
            allKeyWord.add(word);
            //??????????????????????????????
            keyWordStr += word + ",";
        }
        for (String word : hamTopN) {
            allKeyWord.add(word);
            //??????????????????????????????
            keyWordStr += word + ",";
        }

        //???topN???????????????????????????
        System.out.println("**********************?????????TOP2N********************");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(TOP2N_PATH);
            printWriter.write(keyWordStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
        }

        //??????????????????????????????????????????????????????
        System.out.println("**********************??????????????????????????????????????????????????????********************");
        ArrayList<LabeledPoint> spamLabeledPointList = getTrainData(keySpamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList = getTrainData(keyHamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList.addAll(hamLabeledPointList);
        // ????????????????????????rdd
        RDD<LabeledPoint> trainRDD = javaSparkContext.parallelize(spamLabeledPointList).rdd();
        // ????????????????????????
        System.out.println("**********************????????????********************");
        NaiveBayesModel modelNB = NaiveBayes.train(trainRDD);
        LogisticRegressionWithLBFGS logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS();
        LogisticRegressionModel modelLR = logisticRegressionWithLBFGS.setNumClasses(2).run(trainRDD);
        System.out.println("**********************???????????????********************");
        //????????????????????????????????????????????????????????????????????????
        boolean b = FileUtils.deleteQuietly(new File(MODEL_PATH_NB));
        modelNB.save(javaSparkContext.sc(), MODEL_PATH_NB);
        boolean b2 = FileUtils.deleteQuietly(new File(MODEL_PATH_LR));
        modelLR.save(javaSparkContext.sc(), MODEL_PATH_LR);

        //?????????????????????
        //??????????????????????????????????????????????????????
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
            //key?????????????????????
            String key = entry.getKey();
            //value??????????????????
            String value = entry.getValue();
            String path = null;
            //put spam into spamMailList
            if (value.equals("spam") && spamNum_pre < SPAM_NUM_PREDICT) {
                spamNum_pre++;
                System.out.println("???????????????" + (k_pre++) + "????????????" + key + "\t???????????????" + value);
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
                System.out.println("???????????????" + (k_pre++) + "????????????" + key + "\t???????????????" + value);
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


        System.out.println("**********************???????????????********************");
        ArrayList<ArrayList<String>> spamWords = HanlpProcess.cutWords(spamMailList_pre);
        System.out.println(spamWords);
        ArrayList<ArrayList<String>> hamWords = HanlpProcess.cutWords(hamMailList_pre);
        System.out.println(hamWords);
        //??????????????????????????????????????????????????????
        System.out.println("**********************??????????????????????????????????????????????????????********************");
        //1?????????????????????0???????????????????????????????????????
        ArrayList<LabeledPoint> spamLabeledPointList_pre = getTrainData(spamWords, allKeyWord, 1.0, FEATURE_NUM);
        ArrayList<LabeledPoint> hamLabeledPointList_pre = getTrainData(hamWords, allKeyWord, 0.0, FEATURE_NUM);
        spamLabeledPointList_pre.addAll(hamLabeledPointList_pre);
        // ????????????????????????rdd
//        RDD<LabeledPoint> preRDD = javaSparkContext.parallelize(spamLabeledPointList_pre).rdd();
        //??????????????????????????????
        System.out.println("********************??????????????????************************");
        NaiveBayesModel naiveBayesModel = NaiveBayesModel.load(javaSparkContext.sc(), MODEL_PATH_NB);
        LogisticRegressionModel logisticRegressionModel = LogisticRegressionModel.load(javaSparkContext.sc(), MODEL_PATH_LR);
        //????????????????????????email???????????????????????????
        int index = 0;
        int wrong = 0;
        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;
        for (LabeledPoint labeledPoint : spamLabeledPointList_pre) {
            double predictNB = naiveBayesModel.predict(labeledPoint.features());
            double predictLR = logisticRegressionModel.predict(labeledPoint.features());
            if (predictLR == predictNB){
                //????????????????????????????????????????????????????????????
                if (labeledPoint.label() == 1.0 && predictNB == 1.0) {
                    //TP
                    TP++;
                } else if (labeledPoint.label() == 0.0 && predictNB == 0.0) {
                    TN++;
                }else if (labeledPoint.label() == 1.0 && predictNB == 0.0) {
                    FP++;
                } else if (labeledPoint.label() == 0.0 && predictNB == 1.0) {
                    FN++;
                }

            }else {
//                ?????????????????????????????????????????????????????????????????????????????????
                Vector vectorNB = naiveBayesModel.predictProbabilities(labeledPoint.features());
                double rawPreNB = Arrays.stream(vectorNB.toArray()).max().getAsDouble();
                double rawPreLR = MyLogisticRegressionModel.predictPoint(labeledPoint.features(), logisticRegressionModel.weights(), logisticRegressionModel.intercept());
                if (rawPreNB >= rawPreLR){
                    //?????????????????????????????????????????????????????????
                    if (labeledPoint.label() == 1.0 && predictNB == 1.0) {
                        //TP
                        TP++;
                    } else if (labeledPoint.label() == 0.0 && predictNB == 0.0) {
                        TN++;
                    }else if (labeledPoint.label() == 1.0 && predictNB == 0.0) {
                        FP++;
                    } else if (labeledPoint.label() == 0.0 && predictNB == 1.0) {
                        FN++;
                    }
                }else {
                    //?????????????????????????????????
                    if (labeledPoint.label() == 1.0 && predictLR == 1.0) {
                        //TP
                        TP++;
                    } else if (labeledPoint.label() == 0.0 && predictLR == 0.0) {
                        TN++;
                    }else if (labeledPoint.label() == 1.0 && predictLR == 0.0) {
                        FP++;
                    } else if (labeledPoint.label() == 0.0 && predictLR == 1.0) {
                        FN++;
                    }
                }
            }
//            System.out.println("?????????????????????" + fileNameList[index++]);
//            System.out.println("?????????:" + labeledPoint.label() + "\t?????????:" + predictValue);
//            String type = "";
//            if (labeledPoint.label() == 0.0) {
//                type = "????????????";
//            } else {
//                type = "????????????";
//            }
//            if (labeledPoint.label() == predictValue) {
//                System.out.println("????????????:" + type + "\t????????????");
//            } else {
//                System.out.println("????????????:" + type + "\t????????????");
//                wrong++;
//            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        DecimalFormat f1Format = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy = Double.valueOf(decimalFormat.format((double) (TP+TN) / spamLabeledPointList_pre.size())) * 100;
        double precision = Double.valueOf(decimalFormat.format(((double) TP / (TP + FP)))) * 100;
        double recall = Double.valueOf(decimalFormat.format(((double) TP / (TP + FN)))) * 100;
        double F1 = Double.valueOf(f1Format.format((2 * precision * recall) / (precision + recall)));
        System.out.println("======================");
        System.out.println("???????????????????????????" + spamLabeledPointList_pre.size() + "????????????\r\n" +
                        "???????????????" + (spamLabeledPointList_pre.size() - wrong) + "????????????\r\n" +
                        "???????????????" + wrong + "????????????\r\n" +
                        "?????????????????????" + accuracy + "???\r\n"
//                "?????????????????????" + decimalFormat.format((double)wrong / labeledPoints.size()) + "???"
        );
        System.out.println("TP = " + TP);
        System.out.println("FP = " + FP);
        System.out.println("TN = " + TN);
        System.out.println("FN = " + FN);
        System.out.println("????????? = " + accuracy);
        System.out.println("????????? = " + precision);
        System.out.println("????????? = " + recall);
        System.out.println("F1 = " + F1);
//        EvaluationAlgorithm evaluationAlgTable = new EvaluationAlgorithm();
//        evaluationAlgTable.setId("TFIDFNBLR");
//        evaluationAlgTable.setAccuracy((float) accuracy);
//        evaluationAlgTable.setPre((float) precision);
//        evaluationAlgTable.setRecall((float) recall);
//        evaluationAlgTable.setF1((float) F1);
//        evaluationAlgTable.setFn(FN);
//        evaluationAlgTable.setFp(FP);
//        evaluationAlgTable.setTn(TN);
//        evaluationAlgTable.setTp(TP);
//        evaluationAlgTable.setHamTag("ham");
//        evaluationAlgTable.setSpamTag("spam");
//        evaluationAlgTable.setTotalNumHam(HAM_NUM_PREDICT);
//        evaluationAlgTable.setTotalNumSpam(SPAM_NUM_PREDICT);
//        boolean b3 = evaluationAlgService.saveOrUpdate(evaluationAlgTable);
        System.out.println("==========????????????============");

    }

    /*****************get TopN keywords******************************/
    public static List<String> getTopN(ArrayList<ArrayList<String>> keywordList, Integer featureNum) {
        //?????????????????????????????????
        Map<String, Float> tf = MyTFIDF.tfCalculate(keywordList);
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(tf.entrySet());
        //?????????????????????????????????????????????????????????
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        System.out.println(list);
        List<String> listTopN = new ArrayList<>();
        //???N???TF???????????????
        for (Map.Entry<String, Float> m : list) {
            listTopN.add(m.getKey());
            if (listTopN.size() == featureNum) {
                break;
            }
        }
        return listTopN;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param
     * @param keyWords
     * @param type
     * @return
     */
    public static ArrayList<LabeledPoint> getTrainData(ArrayList<ArrayList<String>> wordList, ArrayList<String> keyWords, double type, Integer featureNum) {
        // ???????????????????????????
        int i = 1;
        ArrayList<LabeledPoint> featureBox = new ArrayList<LabeledPoint>();
        for (ArrayList<String> emailMeta : wordList) {
            System.out.println("********????????????" + i + "?????????????????????*********");
            // ??????????????????
            double[] mapTrain = new double[featureNum];
            int mapIndex = 0;
            for (String key : keyWords) {
                if (emailMeta.contains(key)) {
                    //???????????????????????????????????????????????????
                    mapTrain[mapIndex] = 1.0;
                }
                mapIndex++;
            }
            featureBox.add(new LabeledPoint(type, Vectors.dense(mapTrain)));
        }
        return featureBox;
    }

}
