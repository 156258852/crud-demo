package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 服务接口
 */
public interface JwtService {

    /**
     * 生成 Token
     */
    String generateToken(UserDetails userDetails);

    /**
     * 生成 Token（带额外 claims）
     */
    String generateToken(String username, String role);

    /**
     * 从 Token 中提取用户名
     */
    String extractUsername(String token);

    /**
     * 从 Token 中提取过期时间
     */
    Date extractExpiration(String token);

    /**
     * 从 Token 中提取指定 claim
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * 验证 Token 是否有效
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
