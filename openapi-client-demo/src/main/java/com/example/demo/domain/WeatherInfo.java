package com.example.demo.domain;

/**
 * 领域层：核心实体（自己的数据结构，跟下游无关）
 */
public record WeatherInfo(
        String city,
        double temperature,
        String description,
        int humidity,
        String date
) {
}
