package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {

    private final WebClient.Builder webClientBuilder;
    
    @Value("${external.api.base-url:https://jsonplaceholder.typicode.com}")
    private String externalApiBaseUrl;

    /**
     * 获取外部API的用户信息
     */
    public Mono<JsonNode> getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);
        
        return webClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build()
                .get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(JsonNode.class)// 将响应体转换为JsonNode
                .doOnSuccess(user -> log.info("Successfully retrieved user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error retrieving user: ", error));
    }

    /**
     * 获取外部API的所有用户
     */
    public Flux<JsonNode> getAllUsers() {
        log.info("Fetching all users");
        
        return webClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build()
                .get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnNext(user -> log.info("Retrieved user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error retrieving users: ", error));
    }

    /**
     * 获取外部API的帖子信息
     */
    public Flux<JsonNode> getPostsByUserId(Long userId) {
        log.info("Fetching posts for user: {}", userId);
        
        return webClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnNext(post -> log.info("Retrieved post: {}", post.get("title").asText()))
                .doOnError(error -> log.error("Error retrieving posts: ", error));
    }

    /**
     * 调用外部API创建资源
     */
    public Mono<JsonNode> createUser(Map<String, Object> userData) {
        log.info("Creating user with data: {}", userData);

        return webClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build()
                .post()
                .uri("/users")
                .bodyValue(userData)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(user -> log.info("Successfully created user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error creating user: ", error));
    }
}