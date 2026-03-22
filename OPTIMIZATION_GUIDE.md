# 项目优化指南

本文档记录了 Todo CRUD 项目的所有优化内容、修改的文件以及优化效果。

---

## 目录

1. [分页支持](#1-分页支持)
2. [DTO 模式](#2-dto-模式)
3. [Actuator 监控](#3-actuator-监控)
4. [Swagger JWT 配置](#4-swagger-jwt-配置)
5. [优化效果总结](#5-优化效果总结)

---

## 1. 分页支持

### 优化说明

为 Todo API 添加分页功能，避免一次性返回大量数据，提升性能和用户体验。

### 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `TodoService.java` | 新增 `getAllTodos(Pageable pageable)` 方法，返回 `Page<TodoResponse>` |
| `TodoController.java` | 修改 `all()` 方法，支持 `page`、`size`、`sortBy`、`sortDir` 参数 |

### 新增 API 参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | int | 0 | 页码（从 0 开始） |
| `size` | int | 10 | 每页大小 |
| `sortBy` | String | id | 排序字段 |
| `sortDir` | String | asc | 排序方向（asc/desc） |

### 使用示例

```bash
# 获取第一页，每页 10 条，按 id 升序
curl http://localhost:8080/api/todo?page=0&size=10&sortBy=id&sortDir=asc

# 获取第二页，每页 20 条，按创建时间降序
curl http://localhost:8080/api/todo?page=1&size=20&sortBy=id&sortDir=desc

# 获取所有数据（不分页）
curl http://localhost:8080/api/todo/all
```

### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      { "id": 1, "text": "学习 Spring Boot", "done": false }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "number": 0,
    "size": 10,
    "first": true,
    "last": false
  }
}
```

---

## 2. DTO 模式

### 优化说明

引入 DTO（Data Transfer Object）模式，将请求和响应对象与实体类分离，提高代码的可维护性和安全性。

### 新增的文件

| 文件 | 说明 |
|------|------|
| `dto/CreateTodoRequest.java` | 创建待办事项请求 DTO |
| `dto/UpdateTodoRequest.java` | 更新待办事项请求 DTO |
| `dto/TodoResponse.java` | 待办事项响应 DTO |

### DTO 设计原则

```
┌─────────────────────┐     ┌─────────────────────┐
│   CreateTodoRequest │     │   UpdateTodoRequest │
│   (请求 DTO)         │     │   (请求 DTO)         │
└─────────┬───────────┘     └─────────┬───────────┘
          │                           │
          ▼                           ▼
┌─────────────────────────────────────────────────┐
│                  TodoController                  │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                   TodoService                    │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                    Todo (Entity)                 │
│                    数据库实体                     │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                  TodoResponse                    │
│                  (响应 DTO)                       │
└─────────────────────────────────────────────────┘
```

### DTO 类定义

#### CreateTodoRequest

```java
@Data
@Schema(description = "创建待办事项请求")
public class CreateTodoRequest {
    @NotBlank(message = "待办事项内容不能为空")
    @Size(max = 500, message = "待办事项内容不能超过500个字符")
    private String text;

    private Boolean done = false;
}
```

#### UpdateTodoRequest

```java
@Data
@Schema(description = "更新待办事项请求")
public class UpdateTodoRequest {
    @NotBlank(message = "待办事项内容不能为空")
    @Size(max = 500, message = "待办事项内容不能超过500个字符")
    private String text;

    private Boolean done;
}
```

#### TodoResponse

```java
@Data
@Builder
@Schema(description = "待办事项响应")
public class TodoResponse {
    private Long id;
    private String text;
    private Boolean done;
}
```

### 优势

| 优势 | 说明 |
|------|------|
| **解耦** | 实体类变更不影响 API 接口 |
| **安全** | 隐藏实体内部字段，防止敏感信息泄露 |
| **验证** | 请求验证逻辑集中在 DTO 中 |
| **文档** | Swagger 文档更加清晰准确 |

---

## 3. Actuator 监控

### 优化说明

集成 Spring Boot Actuator，提供生产级监控和管理能力。

### 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `pom.xml` | 添加 `spring-boot-starter-actuator` 依赖 |
| `application.yml` | 添加 Actuator 配置 |

### 添加的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 配置说明

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers  # 暴露的端点
      base-path: /actuator                         # 端点基础路径
  endpoint:
    health:
      show-details: when_authorized                # 健康详情显示策略
      probes:
        enabled: true                              # 启用健康探针
    info:
      enabled: true
    metrics:
      enabled: true
  info:
    env:
      enabled: true
```

### 可用的端点

| 端点 | URL | 说明 |
|------|-----|------|
| Health | `/actuator/health` | 应用健康状态 |
| Info | `/actuator/info` | 应用信息 |
| Metrics | `/actuator/metrics` | 应用指标 |
| Env | `/actuator/env` | 环境变量 |
| Loggers | `/actuator/loggers` | 日志配置 |

### 使用示例

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 查看应用信息
curl http://localhost:8080/actuator/info

# 查看所有可用指标
curl http://localhost:8080/actuator/metrics

# 查看特定指标（如 JVM 内存）
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# 动态修改日志级别
curl -X POST http://localhost:8080/actuator/loggers/com.example.demo \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### 健康检查响应示例

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

---

## 4. Swagger JWT 配置

### 优化说明

完善 Swagger UI 的 JWT 认证配置，支持在文档页面直接进行认证测试。

### 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `SwaggerConfig.java` | 添加 SecurityScheme 和 SecurityRequirement 配置 |
| `TodoController.java` | 添加 `@SecurityRequirement(name = "Bearer")` 注解 |

### 配置代码

```java
@Bean
public OpenAPI customOpenAPI() {
    final String securitySchemeName = "Bearer";

    return new OpenAPI()
            .info(new Info()
                    .title("Todo API")
                    .version("1.0.0")
                    .description("..."))
            .components(new Components()
                    .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("请输入 JWT Token")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
}
```

### 使用步骤

1. 访问 Swagger UI：`http://localhost:8080/swagger-ui.html`
2. 点击右上角 🔓 **Authorize** 按钮
3. 输入 JWT Token（格式：`Bearer your_token_here`）
4. 点击 **Authorize** 确认
5. 现在可以测试需要认证的 API

### 认证流程

```
┌─────────────────┐
│  1. 注册用户     │  POST /api/auth/register
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  2. 登录获取Token│  POST /api/auth/login → JWT Token
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  3. Swagger认证  │  点击 Authorize → 输入 Token
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  4. 测试API      │  调用受保护的 API
└─────────────────┘
```

---

## 5. 优化效果总结

### 性能优化

| 优化项 | 效果 |
|--------|------|
| 分页支持 | 减少单次响应数据量，降低网络传输和内存消耗 |
| DTO 模式 | 减少不必要的字段序列化，优化响应体大小 |

### 安全优化

| 优化项 | 效果 |
|--------|------|
| DTO 模式 | 隐藏实体内部字段，防止敏感信息泄露 |
| Swagger JWT | 支持安全的 API 测试，Token 认证集成 |

### 可维护性优化

| 优化项 | 效果 |
|--------|------|
| DTO 模式 | 请求/响应与实体解耦，便于独立演进 |
| Actuator | 提供监控端点，便于问题排查和运维 |

### 开发体验优化

| 优化项 | 效果 |
|--------|------|
| Swagger JWT | 在文档页面直接测试受保护的 API |
| 分页参数 | 灵活的数据查询方式 |
| Actuator | 实时监控应用状态 |

---

## 文件变更清单

### 新增文件

```
src/main/java/com/example/demo/dto/
├── CreateTodoRequest.java    # 创建请求 DTO
├── UpdateTodoRequest.java    # 更新请求 DTO
└── TodoResponse.java         # 响应 DTO
```

### 修改文件

```
├── pom.xml                              # 添加 Actuator 依赖
├── src/main/resources/application.yml   # 添加 Actuator 配置
├── src/main/java/.../config/SwaggerConfig.java      # JWT 认证配置
├── src/main/java/.../controller/TodoController.java # 分页 + DTO
└── src/main/java/.../service/TodoService.java       # 分页 + DTO
```

---

## 测试验证

### 编译测试

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 启动应用

```bash
mvn spring-boot:run
```

### 验证端点

```bash
# 验证 Swagger UI
open http://localhost:8080/swagger-ui.html

# 验证 Actuator
curl http://localhost:8080/actuator/health

# 验证分页 API
curl "http://localhost:8080/api/todo?page=0&size=5"
```

---

## 后续优化建议

1. **缓存支持**：为频繁查询的数据添加 Redis 缓存
2. **异步处理**：使用 `@Async` 处理耗时操作
3. **API 版本控制**：引入版本管理机制
4. **国际化**：添加多语言支持
5. **审计日志**：记录用户操作日志