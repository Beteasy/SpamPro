package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.pojo.CheckRecord;
import com.example.demo.service.SpamPredict;
import com.example.demo.service.TFIDFNBLRPredict;
import com.example.demo.service.impl.MailCheckServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName OneMailCheckController
 * @Description TODO
 * @Author 21971
 * @Date 2021/4/14 14:05
 */
@Controller
public class MailCheckController {
    @Autowired
    SpamPredict spamPredict;

    @Autowired
    TFIDFNBLRPredict tfidfnblrPredict;

    @Autowired
    MailCheckServiceImpl mailCheckService;

//
//    @GetMapping(value = {"/mailbox-compose"})
//    public String oneMailCheckPage(){
//        return "mailbox/mailbox-compose";
//    }



    @PostMapping(value = "/docheck")
    @ResponseBody
    public double checkMail(@RequestParam("mailContent") String mailContent, HttpSession session){
        double result = mailCheckService.checkMail(mailContent, session);
        System.out.println("检测结果为："+result);
        return result;
    }

    //去邮件检测列表
    @GetMapping(value = "/mailbox-main")
    public String toMailBoxMain(HttpServletRequest request, HttpSession session, Model model){
        //这里去数据库去数据放到MODEL中，带回到页面
        Integer currentPage = Integer.valueOf(request.getParameter("currentPage"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        Page<CheckRecord> page = mailCheckService.getCheckRecordsPage(currentPage,limit,session);
        List<CheckRecord> checkRecords = page.getRecords();
        long pages = page.getPages();
        System.out.println("pages"+pages);
        model.addAttribute("checkRecords",checkRecords);
        model.addAttribute("pages",pages);
        return "/mailbox/mailbox-main";
    }


    //去邮件详情
    @GetMapping(value = "/toMailDetail")
    public String toMailDetail(HttpServletRequest request, Model model){
        Integer id = Integer.valueOf(request.getParameter("id"));
        CheckRecord mailDetail =  mailCheckService.getMailDetail(id);
        model.addAttribute("mailDetail",mailDetail);
        return "/mailbox/mailbox-message";
    }

    //删除记录
    @GetMapping(value = "/deleteRecord")
    public String deleteRecord(HttpServletRequest request){
        Integer id = Integer.valueOf(request.getParameter("id"));
        int result = mailCheckService.deleteRecord(id);
        return "redirect:/mailbox-main?currentPage=1&limit=5";
    }

    //修改检测记录
    @GetMapping(value = "/updateCheckRecord")
    @ResponseBody
    public Integer updateRecord(HttpServletRequest request){
        //获取要修改的数据的ID和要修改的数据
        String type = request.getParameter("type");
        Integer mailId = Integer.valueOf(request.getParameter("mailId"));
        int result = mailCheckService.updateCheckRecord(type, mailId);
        //修改后刷新页面——还没实现，先重定向到第一页
        return result;
    }

//
//    @GetMapping(value = "/to-mailbox-main")
//    public String toMailBoxMain
}
