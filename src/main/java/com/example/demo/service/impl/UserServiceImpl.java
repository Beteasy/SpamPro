package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author 21971
 * @Date 2021/5/20 18:12
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public int addUser(User user) {
        int result = userMapper.insert(user);
        return result;
    }

    @Override
    public int userLogin(User user) {
        //去数据库看用户是否存在，以及用户名密码是否正确
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",user.getUsername());
        User userResult = userMapper.selectOne(userQueryWrapper);
        if (userResult == null || !userResult.getPassword().equals(user.getPassword())){
            return 0;
        }else {
            return 1;
        }

    }
}
