package com.example.demo.config;

import com.example.demo.client.petstore.ApiClient;
import com.example.demo.client.petstore.api.PetApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiClientConfig {

    @Value("${petstore.api.base-url:https://petstore.swagger.io/v2}")
    private String petStoreBaseUrl;

    @Bean
    public ApiClient petStoreApiClient() {
        // 创建WebClient实例
        WebClient webClient = WebClient.builder()
            .baseUrl(petStoreBaseUrl) // 使用配置中的PetStore API的基础URL
            .build();

        // 创建ApiClient实例并设置WebClient
        ApiClient apiClient = new ApiClient(webClient);
        return apiClient;
    }

    @Bean
    public PetApi petApi(ApiClient petStoreApiClient) {
        return new PetApi(petStoreApiClient);
    }
}