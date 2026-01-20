package com.example.demo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //作用是自动生成主键值
    private Long id;
    private String text;
    private boolean done;
}