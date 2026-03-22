package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 配置类
 * 配置 API 文档和 JWT 认证支持
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 配置 OpenAPI 文档
     * 包含 JWT Bearer 认证配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // JWT Bearer 认证配置
        final String securitySchemeName = "Bearer";

        return new OpenAPI()
                // API 基本信息
                .info(new Info()
                        .title("Todo API")
                        .version("1.0.0")
                        .description("Todo 待办事项管理 API 文档\n\n" +
                                "## 认证说明\n" +
                                "本 API 使用 JWT Bearer Token 认证。请按以下步骤操作：\n" +
                                "1. 调用 `/api/auth/register` 注册用户\n" +
                                "2. 调用 `/api/auth/login` 获取 JWT Token\n" +
                                "3. 点击右上角 🔓 **Authorize** 按钮\n" +
                                "4. 输入 Token（格式：`Bearer your_token_here`）\n" +
                                "5. 点击 Authorize 确认")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // 服务器配置
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("本地开发服务器")))
                // 安全认证配置
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请输入 JWT Token（格式：Bearer your_token_here）")))
                // 全局安全要求
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}