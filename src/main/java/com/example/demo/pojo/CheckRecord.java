package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

/**
 * @ClassName CheckRecord
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/20 21:42
 */


public class CheckRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String title;
    private String type;
    private String content;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date checkTime;

    public CheckRecord() {
    }

    public CheckRecord(String username, String title, String type, String content, Date checkTime) {
        this.username = username;
        this.title = title;
        this.type = type;
        this.content = content;
        this.checkTime = checkTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }
}
