package com.example.demo.application;

import com.example.demo.domain.WeatherInfo;
import com.example.demo.port.in.WeatherUseCase;
import com.example.demo.port.out.WeatherPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用层：业务逻辑
 * 只依赖 port（接口），不依赖任何具体实现
 */
@Service
public class WeatherService implements WeatherUseCase {

    private final WeatherPort weatherPort;  // ← 只认识出站端口接口

    public WeatherService(WeatherPort weatherPort) {
        this.weatherPort = weatherPort;
    }

    @Override
    public WeatherInfo queryWeather(String city) {
        // 这里可以加业务逻辑：缓存、校验、组合多个数据源等
        return weatherPort.fetchWeather(city);
    }

    @Override
    public List<WeatherInfo> queryForecast(String city, int days) {
        return weatherPort.fetchForecast(city, days);
    }
}
