package com.example.demo.controller;

import com.example.demo.dto.SendCodeRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件控制器
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "邮件管理", description = "邮箱验证码相关接口")
public class EmailController {

    private final EmailService emailService;

    @Value("${email.verification.expiration-minutes:10}")
    private int expirationMinutes;

    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @PostMapping("/send-code")
    public ApiResponse<Map<String, Object>> sendVerificationCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("请求发送验证码到邮箱：{}", request.getEmail());

        emailService.sendVerificationCode(request.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("email", request.getEmail());
        data.put("message", "验证码已发送，请查收邮箱");
        data.put("expirationMinutes", expirationMinutes);

        return ApiResponse.success(data);
    }
}
