package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.EmailLoginRequest;
import com.example.demo.dto.EmailRegisterRequest;
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

    /**
     * 邮箱验证码注册
     */
    AuthResponse registerByEmail(EmailRegisterRequest request);

    /**
     * 邮箱验证码登录
     */
    AuthResponse loginByEmail(EmailLoginRequest request);
}
