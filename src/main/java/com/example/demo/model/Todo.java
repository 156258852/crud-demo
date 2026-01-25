package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@Schema(description = "待办事项对象")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //作用是自动生成主键值
    @Schema(description = "待办事项唯一标识符")
    private Long id;
    
    @Schema(description = "待办事项文本内容", example = "完成项目报告")
    @NotBlank(message = "待办事项内容不能为空")
    @Size(max = 500, message = "待办事项内容不能超过500个字符")
    private String text;
    
    @Schema(description = "是否已完成", example = "false")
    private boolean done;
}