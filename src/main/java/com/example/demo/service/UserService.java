package com.example.demo.service;

import com.example.demo.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    int addUser(User user);
    int userLogin(User user);
}
