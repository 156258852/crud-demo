package com.example.demo.config;

import com.example.demo.generated.ApiClient;
import com.example.demo.generated.api.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 把生成的 client 注册为 Spring Bean
 * baseUrl 从 application.yml 读取，下游换域名改配置就行
 */
@Configuration
public class WeatherClientConfig {

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Bean
    public DefaultApi defaultApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl);

        return new DefaultApi(apiClient);
    }
}
