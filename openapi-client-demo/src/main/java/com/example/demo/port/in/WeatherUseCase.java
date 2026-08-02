package com.example.demo.port.in;

import com.example.demo.domain.WeatherInfo;

import java.util.List;

/**
 * 入站端口：定义业务用例（"系统能做什么"）
 */
public interface WeatherUseCase {

    WeatherInfo queryWeather(String city);

    List<WeatherInfo> queryForecast(String city, int days);
}
