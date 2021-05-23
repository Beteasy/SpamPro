package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.CheckRecord;

import javax.servlet.http.HttpSession;
import java.util.List;


public interface MailCheckService extends IService<CheckRecord> {

    double checkMail(String mailContent, HttpSession session);

    Page<CheckRecord> getCheckRecordsPage(Integer currentPage, Integer limit, HttpSession session);

    CheckRecord getMailDetail(Integer id);
    int deleteRecord(Integer id);
    int updateCheckRecord(String type, Integer mailId);

}
