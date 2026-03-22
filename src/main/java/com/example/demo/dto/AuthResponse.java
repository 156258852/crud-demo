package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "认证响应")
public class AuthResponse {

    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token 类型", example = "Bearer")
    private String type;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "用户角色", example = "USER")
    private String role;
}