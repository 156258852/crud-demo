package com.example.demo.repository;

import com.example.demo.model.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 邮箱验证码 Repository
 */
@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    /**
     * 根据邮箱查找验证码
     */
    Optional<EmailVerificationCode> findByEmail(String email);

    /**
     * 根据邮箱和验证码查找
     */
    Optional<EmailVerificationCode> findByEmailAndCode(String email, String code);

    /**
     * 删除已过期的验证码
     */
    void deleteByExpireTimeBefore(java.time.LocalDateTime expireTime);
}
