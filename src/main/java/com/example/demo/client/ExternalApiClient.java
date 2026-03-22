package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 外部 API 客户端接口
 */
public interface ExternalApiClient {

    /**
     * 获取外部 API 的用户信息
     */
    Mono<JsonNode> getUserById(Long userId);

    /**
     * 获取外部 API 的所有用户
     */
    Flux<JsonNode> getAllUsers();

    /**
     * 获取外部 API 的帖子信息
     */
    Flux<JsonNode> getPostsByUserId(Long userId);

    /**
     * 调用外部 API 创建资源
     */
    Mono<JsonNode> createUser(Map<String, Object> userData);
}
