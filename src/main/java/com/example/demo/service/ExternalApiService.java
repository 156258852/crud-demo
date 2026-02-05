package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient externalApiClient;

    public Mono<JsonNode> getUserById(Long id) {
        return externalApiClient
                .get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Successfully retrieved user with id: {}", id))
                .doOnError(error -> log.error("Error retrieving user with id: {}, error: {}", id, error.getMessage()));
    }

    public Flux<JsonNode> getAllUsers() {
        return externalApiClient
                .get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnNext(response -> log.info("Retrieved user: {}", response.get("id")))
                .doOnError(error -> log.error("Error retrieving users, error: {}", error.getMessage()));
    }

    public Flux<JsonNode> getPostsByUserId(Long id) {
        return externalApiClient
                .get()
                .uri("/users/" + id + "/posts")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnNext(response -> log.info("Retrieved post: {}", response.get("id")))
                .doOnError(error -> log.error("Error retrieving posts for user id: {}, error: {}", id, error.getMessage()));
    }

    public Mono<JsonNode> createUser(Map<String, Object> userData) {
        return externalApiClient
                .post()
                .uri("/users")
                .bodyValue(userData)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Successfully created user: {}", response.get("id")))
                .doOnError(error -> log.error("Error creating user, error: {}", error.getMessage()));
    }
}