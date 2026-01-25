package com.example.demo.controller;

import com.example.demo.client.ExternalApiService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
@Tag(name = "外部API调用", description = "用于调用外部API的接口")
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @Operation(summary = "获取外部用户信息", description = "根据ID获取外部API的用户信息")
    @GetMapping("/user/{id}")
    public Mono<JsonNode> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return externalApiService.getUserById(id);
    }

    @Operation(summary = "获取所有外部用户", description = "获取外部API的所有用户信息")
    @GetMapping("/users")
    public Flux<JsonNode> getAllUsers() {
        return externalApiService.getAllUsers();
    }

    @Operation(summary = "获取用户的帖子", description = "根据用户ID获取该用户的所有帖子")
    @GetMapping("/user/{id}/posts")
    public Flux<JsonNode> getPostsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return externalApiService.getPostsByUserId(id);
    }

    @Operation(summary = "创建外部用户", description = "在外部API中创建新用户")
    @PostMapping("/user")
    public Mono<JsonNode> createUser(@RequestBody Map<String, Object> userData) {
        return externalApiService.createUser(userData);
    }
}