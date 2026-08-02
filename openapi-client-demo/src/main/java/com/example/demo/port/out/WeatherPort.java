package com.example.demo.port.out;

import com.example.demo.domain.WeatherInfo;

import java.util.List;

/**
 * 出站端口：定义外部依赖（"我需要什么外部能力"）
 * 业务代码只认识这个接口，不知道下游是谁
 */
public interface WeatherPort {

    WeatherInfo fetchWeather(String city);

    List<WeatherInfo> fetchForecast(String city, int days);
}
