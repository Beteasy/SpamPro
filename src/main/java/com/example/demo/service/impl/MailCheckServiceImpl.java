package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.MailCheckMapper;
import com.example.demo.pojo.CheckRecord;
import com.example.demo.pojo.User;
import com.example.demo.service.MailCheckService;
import com.example.demo.service.SpamPredict;
import com.example.demo.service.TFIDFNBLRPredict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName MailCheckServiceImpl
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/20 22:18
 */

@Service
public class MailCheckServiceImpl extends ServiceImpl<MailCheckMapper, CheckRecord> implements MailCheckService {

    @Autowired
    SpamPredict spamPredict;

    @Autowired
    TFIDFNBLRPredict tfidfnblrPredict;

    @Autowired
    MailCheckMapper mailCheckMapper;

    @Override
    public double checkMail(String mailContent, HttpSession session) {
        //进行检测
        double result = tfidfnblrPredict.checkMail(mailContent);
        //将检测结果写到数据库
        CheckRecord checkRecord = new CheckRecord();
        User user = (User) session.getAttribute("user");
        checkRecord.setUsername(user.getUsername());
        checkRecord.setTitle(mailContent.substring(0,20));
        checkRecord.setContent(mailContent);
        if (result == 1.0){
            checkRecord.setType("垃圾邮件");
        }else {
            checkRecord.setType("正常邮件");
        }
        mailCheckMapper.insert(checkRecord);
        return result;
    }

    @Override
    public Page<CheckRecord> getCheckRecordsPage(Integer currentPage, Integer limit, HttpSession session) {
        User user = (User)session.getAttribute("user");
        QueryWrapper<CheckRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        Page<CheckRecord> page = new Page<>(currentPage, limit);
        Page<CheckRecord> result = mailCheckMapper.selectPage(page, queryWrapper);
        return result;
    }

    @Override
    public CheckRecord getMailDetail(Integer id) {
        QueryWrapper<CheckRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        CheckRecord checkRecord = mailCheckMapper.selectOne(queryWrapper);
        return checkRecord;
    }

    @Override
    public int deleteRecord(Integer id) {
        int result = mailCheckMapper.deleteById(id);
        return result;
    }

    @Override
    public int updateCheckRecord(String type, Integer mailId) {
        CheckRecord checkRecord = new CheckRecord();
        checkRecord.setId(mailId);
        checkRecord.setType(type);
        int result = mailCheckMapper.updateById(checkRecord);
        return result;
    }
}
