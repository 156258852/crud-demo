package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 邮箱验证码实体
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification_codes")
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 邮箱地址
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 验证码
     */
    @Column(nullable = false, length = 10)
    private String code;

    /**
     * 过期时间
     */
    @Column(nullable = false)
    private LocalDateTime expireTime;

    /**
     * 是否已使用
     */
    @Column(nullable = false)
    private boolean used = false;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    /**
     * 检查验证码是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 检查验证码是否有效
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
}
