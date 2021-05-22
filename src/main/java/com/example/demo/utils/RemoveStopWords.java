package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @ClassName RemoveStopWords
 * @Description TODO
 * @Author 21971
 * @Date 2021/1/28 14:55
 */
public class RemoveStopWords {

//    public static ArrayList<String> getKeyWordsList(ArrayList<String> wordsList){
//        /**
//         * @MethodName getKeyWordsList
//         * @Description TODO   旧版去除停词
//         * @Author 21971
//         * @param wordsList
//         * @Date 2021/2/5 11:21
//         */
////        ArrayList<String> keyWordsList  = new ArrayList<>();
//        ArrayList<String> stopWordsList = new ArrayList<>();
//        String oneStopWord = null;
//        String path = new String("src/main/resources/hit_stopwords.txt");
////        String path = new String("src/main/resources/stopwords.txt");
//
//        try {
//            File stopWordsTable = new File(path);
//            FileReader fileReader = new FileReader(stopWordsTable);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            while ((oneStopWord = bufferedReader.readLine()) != null){
//                stopWordsList.add(oneStopWord.trim());
//            }
//            wordsList.removeAll(stopWordsList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return wordsList;
//    }
public static ArrayList<ArrayList<String>> getKeyWordsList(ArrayList<ArrayList<String>> wordsList){
    /**
     * @MethodName getKeyWordsList
     * @Description TODO   新版去除停词
     * @Author 21971
     * @param wordsList
     * @Date 2021/2/5 11:21
     */
//        ArrayList<String> keyWordsList  = new ArrayList<>();
    //读取停词列表
    ArrayList<String> stopWordsList = new ArrayList<>();
    String oneStopWord = null;
    String path = new String("src/main/resources/hit_stopwords.txt");
    try {
        File stopWordsTable = new File(path);
        FileReader fileReader = new FileReader(stopWordsTable);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((oneStopWord = bufferedReader.readLine()) != null){
            stopWordsList.add(oneStopWord);
        }
//        System.out.println("/****************stopWordsList********************/");
//        System.out.println(stopWordsList);
        //去除停词----需要遍历去除，直接去不了
        for (int i=0; i<wordsList.size(); i++){
            System.out.println("移除第"+i+"封邮件停用词");
            wordsList.get(i).removeAll(stopWordsList);
//            System.out.println(wordsList.get(i));
            //过滤掉长度为1和长度超过12的词组
            for (int j=0; j<wordsList.get(i).size();j++){
                if (wordsList.get(i).get(j).length()<2 || wordsList.get(i).get(j).length()>12){
                    wordsList.get(i).remove(wordsList.get(i).get(j));
                }
            }

        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return wordsList;
}
}
