package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);
}
