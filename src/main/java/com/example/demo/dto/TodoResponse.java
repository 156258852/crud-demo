package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 待办事项响应 DTO
 * 用于返回待办事项数据给客户端
 * 隐藏实体内部细节，提供清晰的 API 响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "待办事项响应")
public class TodoResponse {

    @Schema(description = "待办事项唯一标识符", example = "1")
    private Long id;

    @Schema(description = "待办事项文本内容", example = "完成项目报告")
    private String text;

    @Schema(description = "是否已完成", example = "false")
    private Boolean done;
}