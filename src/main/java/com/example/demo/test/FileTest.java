//package com.example.demo.test;
//
//
//import com.example.demo.service.SpamPredict;
//import com.example.demo.service.TFIDFNBLRPredict;
//import com.example.demo.utils.HanlpProcess;
//import com.example.demo.utils.ProcessFile;
//import com.example.demo.utils.RemoveStopWords;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Map;
//
///**
// * @ClassName Test
// * @Description TODO
// * @Author 21971
// * @Date 2021/1/26 15:01
// */
//public class FileTest {
//    @Test
//    void readFileTest(){
////        ArrayList<String> mailList = ProcessFile.readFiles("E:\\FinalProject\\datasets\\trec06c\\data\\000");
////        ArrayList<String> mailList = ProcessFile.readFiles("C:\\Users\\21971\\Desktop\\test");
////        ArrayList<String> mailList = ProcessFile.readFile("E:\\FinalProject\\datasets\\trec06c\\data\\000");
////        System.out.println(mailList);
////        ArrayList<String> bodyList = ProcessFile.getBody(mailList);
////        ArrayList<String> wordList = HanlpProcess.cutWords(bodyList);
////        try {
////            ArrayList<String> keyWordsList = RemoveStopWords.getKeyWordsList(wordList);
////            System.out.println(keyWordsList);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
//
////        //test function-> ProcessFile.getTypeAndPath
////        String fullPath = "E:\\FinalProject\\datasets\\trec06c\\full\\index";
////        Map<String, String> typeAndPathMap = ProcessFile.getTypeAndPath(fullPath);
////        Iterator<Map.Entry<String,String>> it = typeAndPathMap.entrySet().iterator();
////        System.out.println(typeAndPathMap.get("../data/000/000"));
//
//
//
//        //test function: ProcessFile.readFile
//
//
//    }
//
//
//    @Test
//    void preTest(){
//        String email = "正规發剽【驗证后付款】有保障。\n" +
//                "                   \n" +
//                "\n" +
//                "           電话微信：1987-4764-060  陈\n" +
//                "                         \n" +
//                "                        酒店住宿、餐饮、建材等等都有\n" +
//                "\n" +
//                " \n" +
//                "\n" +
//                " \n" +
//                "\n" +
//                "a touch of her fan on my cheek; could I not understand！ Was I still such a child， Must I be broken more harshly in to learn to give place! That was all.\n" +
//                "還如一夢中彭游\n" +
//                "能解连环";
////        SpamPredict.textCheck(email);
//    }
//
//
//    @Test
//    void typeAndPathTest(){
//        Map<String, String> typeAndPath = ProcessFile.getTypeAndPath("E:\\FinalProject\\datasets\\trec06c\\full\\index");
//        for (Map.Entry<String,String> entry:typeAndPath.entrySet()){
//            String key = entry.getKey();
//            String value = entry.getValue();
//            System.out.println(key);
//        }
////        ProcessFile.getTypeAndPath("E:\\FinalProject\\datasets\\trec06c\\full\\index");
//    }
//
//
////    @Test
////    void fileCheckTest(){
////        SpamPredict.fileCheck();
////    }
//
//
//    @Test
//    void cutWordsTest(){
//        String mail = "贵公司负责人(经理/财务)您好：\n" +
//                "\n" +
//                "    我公司是深圳市东讯实业有限公司，我公司实力雄厚，有着良好的社会关系。\n" +
//                "因进项较多现完成不了每月销售额度，每月有一部分普通商品销售发票(国税）、\n" +
//                "1.5%、普通发票（地税)(1.5%),优惠代开与合作,\n" +
//                "还可以根据贵公司要求代开的数量额度来商讨代开优惠的点数,本公司郑重承诺\n" +
//                "以上所用绝对是真票。\n" +
//                "\n" +
//                "    如贵公司在发票的真伪方面有任何疑虑或担心可上网查证（先用票后付款）\n" +
//                "\n" +
//                "    \n" +
//                "\n" +
//                "                 详情请电:13686411777      \n" +
//                "                 联 系 人:梁先生      ";
//
//        ArrayList<String> mailList = new ArrayList<>();
//        mailList.add(mail);
//        ArrayList<ArrayList<String>> lists = HanlpProcess.cutWords(mailList);
//        ArrayList<ArrayList<String>> keyWordsList = RemoveStopWords.getKeyWordsList(lists);
//        System.out.println(keyWordsList);
//
//
//    }
//
//    @Test
//    void fileTest(){
//        SpamPredict spamPredict = new SpamPredict();
//        spamPredict.fileCheck();
//    }
//
//    @Test
//    void TFIDFNBLRTest(){
//        String mail = "贵公司负责人(经理/财务)您好：\n" +
//                "\n" +
//                "    我公司是深圳市东讯实业有限公司，我公司实力雄厚，有着良好的社会关系。\n" +
//                "因进项较多现完成不了每月销售额度，每月有一部分普通商品销售发票(国税）、\n" +
//                "1.5%、普通发票（地税)(1.5%),优惠代开与合作,\n" +
//                "还可以根据贵公司要求代开的数量额度来商讨代开优惠的点数,本公司郑重承诺\n" +
//                "以上所用绝对是真票。\n" +
//                "\n" +
//                "    如贵公司在发票的真伪方面有任何疑虑或担心可上网查证（先用票后付款）\n" +
//                "\n" +
//                "    \n" +
//                "\n" +
//                "                 详情请电:13686411777      \n" +
//                "                 联 系 人:梁先生      ";
//        TFIDFNBLRPredict predict = new TFIDFNBLRPredict();
//        double result = predict.checkMail(mail);
//        System.out.println(result);
//    }
//
//
//
//}
