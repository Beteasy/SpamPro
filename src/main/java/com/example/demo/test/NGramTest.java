//package com.example.demo.test;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.sql.SparkSession;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @ClassName NGramTest
// * @Description TODO
// * @Author 21971
// * @Date 2021/5/12 14:53
// */
//public class NGramTest {
//
////    @Test
////    void NgramTest(){
////        SparkConf sparkConf = new SparkConf();
////        sparkConf.setMaster("local[*]").setAppName("ngram");
////        SparkSession sparkSession = SparkSession.builder().config(sparkConf).appName("demo").getOrCreate();
////        sparkSession.createDataFrame(Seq(
////                (0, Array("Hi", "I", "heard", "about", "Spark")),
////                (1, Array("I", "wish", "Java", "could", "use", "case", "classes")),
////        (2, Array("Logistic", "regression", "models", "are", "neat"))
////    )).toDF("id", "words");
////        List<List<String>> list = new ArrayList<>();
////        List<String> list1 = new ArrayList<>();
////        List<String> list2 = new ArrayList<>();
////        list1.add("Hi");
////        list1.add("I");
////        list1.add("heard");
////        list1.add("about");
////        list1.add("Spark");
////        list2.add("I");
////        list2.add("wish");
////        list2.add("Java");
////        list2.add("could");
////        list2.add("use");
////        list2.add("case");
////        list2.add("classes");
////        list.add(list1);
////        list.add(list2);
////        sparkSession.createDataset();
////
////
////    }
//}
