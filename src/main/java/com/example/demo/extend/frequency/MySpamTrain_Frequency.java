package com.example.demo.extend.frequency;

import com.example.demo.utils.HanlpProcess;
import com.example.demo.utils.MyTFIDF;
import com.example.demo.utils.ProcessFile;
import com.example.demo.utils.RemoveStopWords;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @ClassName Excutor
 * @Description TODO
 * @Author 21971
 * @Date 2021/1/30 15:33
 */
public class MySpamTrain_Frequency {
    //the path of index file
    //fowling two paths need to be modified if run on linux
    public static final String FULL_PATH = "E:\\FinalProject\\datasets\\trec06c\\full\\index_train";
    public static final String DATA_PRE_PATH = "E:\\FinalProject\\datasets\\trec06c";
    public static final String MODEL_PATH = "E:\\FinalProject\\models\\frequency";
    public static final String TOP2N_PATH  = "E:\\FinalProject\\datasets\\trec06c\\spam_java.txt";
    //the number of spam used for training
    public static final Integer SPAM_NUM_TRAIN = 3000;
    //the number of ham used for training
    public static final Integer HAM_NUM_TRAIN = 3000;
    public static final Integer FEATURE_NUM = 200;


    public static void main(String[] args) {


        ArrayList<String> spamMailList  = new ArrayList<>();
        ArrayList<String> hamMailList   = new ArrayList<>();
        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("spam");
        JavaSparkContext jsc = new JavaSparkContext(conf);


        /*****************read the type and path of all emails*******************************/
        Map<String, String> typeAndPathMap = ProcessFile.getTypeAndPath(FULL_PATH);


        /**********************read spam as will as ham and put them into spamMailList and hanMailList*****************/
        Integer spamNum = 0;
        Integer hamNum  = 0;
        int k = 1;
        for (Map.Entry<String,String> entry: typeAndPathMap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            String path = null;
            //put spam into spamMailList
            if (value.equals("spam") && (spamNum++)<SPAM_NUM_TRAIN){
                System.out.println("???????????????"+(k++)+"????????????"+key+"\t???????????????"+value);
                path = key.replace("..",DATA_PRE_PATH);
                //remove this sentence when run on linux
                path = path.replace("/", "\\");
                String mail = null;
                mail = ProcessFile.readFile(path);
                spamMailList.add(mail);
            }
            if(value.equals("ham") && (hamNum++)<HAM_NUM_TRAIN) {
                System.out.println("???????????????"+(k++)+"????????????"+key+"\t???????????????"+value);
                path = key.replace("..",DATA_PRE_PATH);
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

        // ???????????????????????????????????????TOPN/2??????
        System.out.println("**********************??????TOPN/2********************");
        List<String> spamTopN = getTopN(keySpamWords,FEATURE_NUM/2);
        List<String> hamTopN = getTopN(keyHamWords,FEATURE_NUM/2);


        // ??????TOPN/2????????????????????????TOPN
        System.out.println("**********************????????????TOPN********************");
        ArrayList<String> allKeyWord = new ArrayList<>();
        String keyWordStr = "";
        for (String word:spamTopN){
            allKeyWord.add(word);
            //??????????????????????????????
            keyWordStr += word + ",";
        }
        for (String word:hamTopN){
            allKeyWord.add(word);
            //??????????????????????????????
            keyWordStr += word + ",";
        }



        //???top200???????????????????????????
        System.out.println("**********************?????????TOP200********************");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(TOP2N_PATH);
            printWriter.write(keyWordStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }

        //??????????????????????????????????????????????????????
        System.out.println("**********************??????????????????????????????????????????????????????********************");
        ArrayList<LabeledPoint> spamLabledPointList = getTrainData(keySpamWords, allKeyWord, 1.0);
        ArrayList<LabeledPoint> hamLabledPointList = getTrainData(keyHamWords, allKeyWord, 0.0);
        spamLabledPointList.addAll(hamLabledPointList);
        // ????????????????????????rdd
        RDD<LabeledPoint> trainRDD = jsc.parallelize(spamLabledPointList).rdd();
        // ????????????????????????
        System.out.println("**********************????????????********************");
        NaiveBayesModel model = NaiveBayes.train(trainRDD);
        System.out.println("**********************???????????????********************");
        boolean b = FileUtils.deleteQuietly(new File(MODEL_PATH));
        model.save(jsc.sc(),MODEL_PATH);

    }

    /*****************get Top100 keywords******************************/
    public static List<String> getTopN(ArrayList<ArrayList<String>> keywordList,Integer featureNum){
        Map<String, Float> tf = MyTFIDF.tfCalculate(keywordList);
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(tf.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        System.out.println(list);
        List<String> listTopN = new ArrayList<>();
        //???N???TF???????????????
        for(Map.Entry<String,Float> m : list){
            listTopN.add(m.getKey());
            if (listTopN.size()==featureNum){
                break;
            }
        }
        return listTopN;
    }
//    public static List<String> getTop100(ArrayList<ArrayList<String>> keywordList, JavaSparkContext jsc){
//        //??????top15
//        ArrayList<List<Tuple2<String,Integer>>> top15List = new ArrayList<List<Tuple2<String,Integer>>>();
//        for (ArrayList<String> s: keywordList){
//            //???email?????????????????????javaRDD
//            JavaRDD<String> emailRDD = jsc.parallelize(s);
//            //???????????????
//            //??????
//            //????????????
//            //??????top15????????????
//            List<Tuple2<String, Integer>> metaList  = emailRDD.mapToPair(new PairFunction<String, String, Integer>() {
//                @Override
//                public Tuple2<String, Integer> call(String s) throws Exception {
//                    return new Tuple2<String,Integer>(s,1);
//                }
//            }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//                @Override
//                public Integer call(Integer integer, Integer integer2) throws Exception {
//                    return integer+integer2;
//                }
//            }).mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
//                @Override
//                public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
//                    return new Tuple2<Integer,String>(stringIntegerTuple2._2(),stringIntegerTuple2._1());
//                }
//            }).sortByKey(false).mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
//                @Override
//                public Tuple2<String, Integer> call(Tuple2<Integer, String> integerStringTuple2) throws Exception {
//                    return new Tuple2<String,Integer>(integerStringTuple2._2(), integerStringTuple2._1());
//                }
//            }).take(15);
//            top15List.add(metaList);
//        }
//        //??????top15???top100
//        ArrayList<Tuple2<String,Integer>> allTuple = new ArrayList<Tuple2<String,Integer>>();
//        for (List<Tuple2<String,Integer>> list1: top15List){
//            allTuple.addAll(list1);
//        }
//
//        //??????????????????????????????javaRDD
//        JavaRDD<Tuple2<String,Integer>> allJRDD = jsc.parallelize(allTuple);
//        // ??????
//        // ??????
//        // ??????
//        // ??????TOP100????????????
//        List<String> top100List = allJRDD.mapToPair(new PairFunction<Tuple2<String, Integer>, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
//                return new Tuple2<String,Integer>(stringIntegerTuple2._1(),stringIntegerTuple2._2());
//            }
//        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer integer, Integer integer2) throws Exception {
//                return integer+integer2;
//            }
//        }).mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
//            @Override
//            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
//                return new Tuple2<Integer,String>(stringIntegerTuple2._2(),stringIntegerTuple2._1());
//            }
//        }).sortByKey(false).map(new Function<Tuple2<Integer, String>, String>() {
//            @Override
//            public String call(Tuple2<Integer, String> integerStringTuple2) throws Exception {
//                return integerStringTuple2._2();
//            }
//        }).take(100);
//
//        return top100List;
//    }

    /**
     * ????????????????????????????????????????????????????????????
     * @param
     * @param keyWords
     * @param type
     * @return
     */
    public static ArrayList<LabeledPoint> getTrainData(ArrayList<ArrayList<String>> wordList, ArrayList<String> keyWords, double type){
        // ???????????????????????????
        int i = 1;
        ArrayList<LabeledPoint> featureBox = new ArrayList<LabeledPoint>();
        for(ArrayList<String> emailMeta:wordList){
            System.out.println("********????????????"+i+"?????????????????????*********");
            // ??????????????????
            double[] mapTrain = new double[200];
            int mapIndex = 0;
            for(String key:keyWords){
                if(emailMeta.contains(key)){
                    mapTrain[mapIndex] = 1.0;
                }
                mapIndex ++;
            }
            System.out.println("###########??????mapTrain??????##################");
            System.out.println(mapTrain);
            featureBox.add(new LabeledPoint(type, Vectors.dense(mapTrain)));
        }
        System.out.println("###########??????featureBox??????##################");
        System.out.println(featureBox);
        return featureBox;
    }
}
