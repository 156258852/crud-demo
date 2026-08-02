package com.example.demo.adapter.out.weather;

import com.example.demo.domain.WeatherInfo;
import com.example.demo.generated.api.DefaultApi;
import com.example.demo.generated.model.ForecastItem;
import com.example.demo.generated.model.WeatherResponse;
import com.example.demo.port.out.WeatherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 出站适配器：调用下游天气服务
 *
 * 这就是你在公司见到的那个 "Adapter" 文件！
 * 它实现 port/out 的接口，内部用 target/ 里生成的 client 调下游
 *
 * 下游换域名 → 改 application.yml
 * 下游改字段 → 重新 mvn compile，改这里的转换逻辑
 * 业务代码（Service）完全不受影响
 */
@Component
public class WeatherAdapter implements WeatherPort {

    private static final Logger log = LoggerFactory.getLogger(WeatherAdapter.class);

    private final DefaultApi generatedApi;  // ← target/ 里自动生成的 client

    public WeatherAdapter(DefaultApi generatedApi) {
        this.generatedApi = generatedApi;
    }

    @Override
    public WeatherInfo fetchWeather(String city) {
        log.info("调用下游天气服务, city={}", city);

        // 1. 用生成的 client 调下游
        WeatherResponse response = generatedApi.getWeather(city).block();

        // 2. 转换：下游 DTO → 自己的领域对象
        return new WeatherInfo(
                response.getCity(),
                response.getTemperature().doubleValue(),
                response.getWeatherDesc(),
                response.getHumidity(),
                null
        );
    }

    @Override
    public List<WeatherInfo> fetchForecast(String city, int days) {
        log.info("调用下游预报服务, city={}, days={}", city, days);

        // 1. 调下游
        List<ForecastItem> items = generatedApi.getForecast(city, days).collectList().block();

        // 2. 转换
        return items.stream()
                .map(item -> new WeatherInfo(
                        city,
                        item.getHighTemp().doubleValue(),
                        item.getWeatherDesc(),
                        0,
                        item.getDate()
                ))
                .toList();
    }
}
