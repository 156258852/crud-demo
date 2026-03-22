package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 更新待办事项请求 DTO
 * 用于接收客户端更新待办事项的请求数据
 */
@Data
@Schema(description = "更新待办事项请求")
public class UpdateTodoRequest {

    @Schema(description = "待办事项文本内容", example = "更新后的待办事项内容")
    @NotBlank(message = "待办事项内容不能为空")
    @Size(max = 500, message = "待办事项内容不能超过500个字符")
    private String text;

    @Schema(description = "是否已完成", example = "true")
    private Boolean done;
}