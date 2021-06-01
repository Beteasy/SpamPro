package com.example.demo;

/**
 * @ClassName DemoApplicationTests
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/31 16:07
 */

import com.example.demo.service.TFIDFNBLRPredict;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    JavaSparkContext javaSparkContext;
    @Autowired
    TFIDFNBLRPredict tfidfnblrPredict;

    @Test
    void contextLoads() {
    }

    @Test
    public void TFIDFNBLRPreTest() {
        String mail = "贵公司负责人(经理/财务)您好：\n" +
                "\n" +
                "    我公司是深圳市东讯实业有限公司，我公司实力雄厚，有着良好的社会关系。\n" +
                "因进项较多现完成不了每月销售额度，每月有一部分普通商品销售发票(国税）、\n" +
                "1.5%、普通发票（地税)(1.5%),优惠代开与合作,\n" +
                "还可以根据贵公司要求代开的数量额度来商讨代开优惠的点数,本公司郑重承诺\n" +
                "以上所用绝对是真票。\n" +
                "\n" +
                "    如贵公司在发票的真伪方面有任何疑虑或担心可上网查证（先用票后付款）\n" +
                "\n" +
                "    \n" +
                "\n" +
                "                 详情请电:13686411777      \n" +
                "                 联 系 人:梁先生      ";
        double result = tfidfnblrPredict.checkMail(mail);
        System.out.println(result);
    }

}
