package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@Schema(description = "用户对象")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "用户唯一标识符")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(unique = true)
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;

    @Schema(description = "用户角色", example = "USER")
    private String role;
}