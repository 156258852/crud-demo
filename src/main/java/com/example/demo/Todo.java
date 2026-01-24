package com.example.demo;

import jakarta.persistence.*;
import lombok.Data;

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
    private String text;
    
    @Schema(description = "是否已完成", example = "false")
    private boolean done;
}