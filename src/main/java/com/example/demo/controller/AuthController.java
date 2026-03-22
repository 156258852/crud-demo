package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.EmailLoginRequest;
import com.example.demo.dto.EmailRegisterRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录接口")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "注册新用户并返回 JWT Token")
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @Operation(summary = "用户登录", description = "用户登录并返回 JWT Token")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @Operation(summary = "邮箱验证码注册", description = "使用邮箱验证码注册新用户并返回 JWT Token")
    @PostMapping("/register/email")
    public ApiResponse<AuthResponse> registerByEmail(@Valid @RequestBody EmailRegisterRequest request) {
        return ApiResponse.success(userService.registerByEmail(request));
    }

    @Operation(summary = "邮箱验证码登录", description = "使用邮箱验证码登录并返回 JWT Token")
    @PostMapping("/login/email")
    public ApiResponse<AuthResponse> loginByEmail(@Valid @RequestBody EmailLoginRequest request) {
        return ApiResponse.success(userService.loginByEmail(request));
    }
}
