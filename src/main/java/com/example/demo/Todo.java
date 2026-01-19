package com.example.demo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue
    private Long id;
    private String text;
    private boolean done;
}