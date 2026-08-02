package com.example.demo.adapter.in.web;

import com.example.demo.domain.WeatherInfo;
import com.example.demo.port.in.WeatherUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 入站适配器：接收 HTTP 请求
 * 只依赖入站端口（WeatherUseCase），不知道具体 Service 实现
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherUseCase weatherUseCase;

    public WeatherController(WeatherUseCase weatherUseCase) {
        this.weatherUseCase = weatherUseCase;
    }

    @GetMapping
    public WeatherInfo getWeather(@RequestParam String city) {
        return weatherUseCase.queryWeather(city);
    }

    @GetMapping("/forecast")
    public List<WeatherInfo> getForecast(
            @RequestParam String city,
            @RequestParam(defaultValue = "3") int days) {
        return weatherUseCase.queryForecast(city, days);
    }
}
