package com.example.demo.client.impl;

import com.example.demo.client.ExternalApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 外部 API 客户端实现类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalApiClientImpl implements ExternalApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.api.base-url:https://jsonplaceholder.typicode.com}")
    private String externalApiBaseUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build();
    }

    @Override
    public Mono<JsonNode> getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);

        return webClient
                .get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(user -> log.info("Successfully retrieved user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error retrieving user: ", error));
    }

    @Override
    public Flux<JsonNode> getAllUsers() {
        log.info("Fetching all users");

        return webClient
                .get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnNext(user -> log.info("Retrieved user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error retrieving users: ", error));
    }

    @Override
    public Flux<JsonNode> getPostsByUserId(Long userId) {
        log.info("Fetching posts for user: {}", userId);

        return webClient
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

    @Override
    public Mono<JsonNode> createUser(Map<String, Object> userData) {
        log.info("Creating user with data: {}", userData);

        return webClient
                .post()
                .uri("/users")
                .bodyValue(userData)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(user -> log.info("Successfully created user: {}", user.get("name").asText()))
                .doOnError(error -> log.error("Error creating user: ", error));
    }
}
