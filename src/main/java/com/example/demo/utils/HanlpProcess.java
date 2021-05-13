package com.example.demo.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HanlpProcess
 * @Description TODO
 * @Author 21971
 * @Date 2021/1/28 10:48
 */
public class HanlpProcess {
//    public static ArrayList<String> cutWords(ArrayList<String> bodyList){
//        /**
//         * @ClassName cutWords
//         * @Description TODO
//         * @Author 21971
//         * @param bodyList
//         * @Date 2021/1/28 11:11
//         * 这种方式是吧所有邮件分词后的结果放到一起
//         */
//        ArrayList<String> wordsList = new ArrayList<>();
//        for (String body: bodyList){
//            //NLP分词
//            //System.out.println(body);
//            List<Term> words = NLPTokenizer.segment(body);
//            //System.out.println(words);
//            //
//            for (Term term:words){
//                String word = term.word;
//                wordsList.add(word);
//            }
//        }
//        return wordsList;
//    }
public static ArrayList<ArrayList<String>> cutWords(ArrayList<String> bodyList){
    /**
     * @ClassName cutWords
     * @Description TODO
     * @Author 21971
     * @param bodyList
     * @Date 2021/1/28 11:11
     * 这种方式是吧每个邮件分词后的结果单独放，最后返回结果相当于是一个二维数组，每一维都是一个ArrayList<String>
     */
    HanLP.Config.ShowTermNature = false;    //测试通过，关闭了词性标注，不需要再遍历每个词组处理
    ArrayList<ArrayList<String>> wordsList = new ArrayList<ArrayList<String>>();
    for (String body: bodyList){
        //NLP分词
//        List<Term> words = NLPTokenizer.segment(body);
        List<String> words = new ArrayList<>();
        try {
            CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
            words = analyzer.segment(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wordsList.add(new ArrayList<>(words));
    }
    return wordsList;
}
}
