package com.example.demo.service;

import com.example.demo.model.EmailVerificationCode;
import com.example.demo.repository.EmailVerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 邮件服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository verificationCodeRepository;

    @Value("${email.verification.code-length:6}")
    private int codeLength;

    @Value("${email.verification.expiration-minutes:10}")
    private int expirationMinutes;

    @Value("${email.verification.from:your-email@qq.com}")
    private String fromEmail;

    /**
     * 发送验证码
     *
     * @param email 收件人邮箱
     * @return 验证码
     */
    public String sendVerificationCode(String email) {
        // 生成验证码
        String code = generateCode();

        // 计算过期时间
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(expirationMinutes);

        // 保存或更新验证码
        EmailVerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElse(new EmailVerificationCode());
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpireTime(expireTime);
        verificationCode.setUsed(false);

        verificationCodeRepository.save(verificationCode);

        // 发送邮件
        sendEmail(email, "邮箱验证码", buildEmailContent(code));

        log.info("验证码已发送到邮箱：{}", email);
        return code;
    }

    /**
     * 验证验证码
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 验证结果
     */
    public boolean verifyCode(String email, String code) {
        return verificationCodeRepository.findByEmailAndCode(email, code)
                .map(verificationCode -> {
                    if (verificationCode.isValid()) {
                        // 标记为已使用
                        verificationCode.setUsed(true);
                        verificationCodeRepository.save(verificationCode);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    /**
     * 生成随机验证码
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 构建邮件内容
     */
    private String buildEmailContent(String code) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .code { background: #fff; border: 2px dashed #667eea; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; color: #667eea; margin: 20px 0; border-radius: 5px; letter-spacing: 5px; }
                        .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 邮箱验证码</h1>
                        </div>
                        <div class="content">
                            <p>您好！</p>
                            <p>您正在请求邮箱验证码，请使用以下验证码完成操作：</p>
                            <div class="code">%s</div>
                            <div class="warning">
                                <strong>⚠️ 重要提示：</strong>
                                <ul style="margin: 10px 0; padding-left: 20px;">
                                    <li>验证码有效期为 %d 分钟</li>
                                    <li>请勿将验证码泄露给他人</li>
                                    <li>如非本人操作，请忽略此邮件</li>
                                </ul>
                            </div>
                            <p>祝您使用愉快！</p>
                        </div>
                        <div class="footer">
                            <p>此邮件由系统自动发送，请勿直接回复</p>
                            <p>&copy; 2024 Todo CRUD Demo. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, code, expirationMinutes);
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true 表示 HTML 内容

            mailSender.send(message);
            log.info("邮件发送成功：{}", to);
        } catch (MessagingException e) {
            log.error("邮件发送失败：{}", to, e);
            throw new RuntimeException("邮件发送失败：" + e.getMessage(), e);
        }
    }

    /**
     * 清理过期的验证码（每小时执行一次）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupExpiredCodes() {
        verificationCodeRepository.deleteByExpireTimeBefore(LocalDateTime.now());
        log.info("已清理过期的验证码");
    }
}
