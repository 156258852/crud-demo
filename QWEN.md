# QWEN.md - Spring Boot Todo CRUD Demo 项目指南

## 项目概述

这是一个面向前端开发者的 Spring Boot BFF（Backend for Frontend）入门示例项目。项目实现了一个完整的 Todo 待办事项 CRUD 应用，帮助前端开发者快速理解后端开发概念。

### 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2.0 |
| 语言 | Java 17 |
| 数据库 | MySQL |
| ORM | Spring Data JPA / Hibernate |
| 文档 | SpringDoc OpenAPI (Swagger UI) |
| 构建工具 | Maven |
| 工具库 | Lombok |
| 响应式客户端 | Spring WebFlux |

### 项目架构

采用经典的三层架构：

```
┌─────────────────┐
│   Controller    │  ← REST API 层，处理 HTTP 请求
├─────────────────┤
│    Service      │  ← 业务逻辑层
├─────────────────┤
│   Repository    │  ← 数据访问层 (JPA)
└─────────────────┘
```

## 构建和运行

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+ (需运行在 localhost:3306)

### 数据库配置

项目使用 MySQL 数据库，配置位于 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=123456
```

数据库 `todo_db` 会自动创建，表结构由 JPA 自动生成（`ddl-auto=update`）。

### 常用命令

```bash
# 编译项目
mvn compile

# 运行应用
mvn spring-boot:run

# 打包
mvn clean package

# 运行测试
mvn test

# 跳过测试打包
mvn clean package -DskipTests
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 应用首页 | http://localhost:8080 |
| API 文档 (Swagger) | http://localhost:8080/swagger-ui.html |
| API Docs JSON | http://localhost:8080/v3/api-docs |

## 项目结构

```
src/main/java/com/example/demo/
├── DemoApplication.java       # 应用启动类
├── aspect/                    # AOP 切面
│   ├── LogExecutionTime.java  # 自定义注解
│   └── ServiceLoggingAspect.java
├── client/                    # 外部 API 客户端
│   └── ExternalApiService.java
├── config/                    # 配置类
│   ├── SwaggerConfig.java
│   ├── WebClientConfig.java
│   └── WebConfig.java         # 拦截器配置
├── controller/                # REST 控制器
│   ├── TodoController.java
│   └── ExternalApiController.java
├── exception/                 # 异常处理
│   ├── GlobalExceptionHandler.java
│   └── TodoNotFoundException.java
├── filter/                    # 过滤器
│   └── RequestLoggingFilter.java
├── interceptor/               # 拦截器
│   └── ApiLoggingInterceptor.java
├── model/                     # 实体类
│   └── Todo.java
├── repository/                # 数据访问层
│   └── TodoRepo.java
└── service/                   # 业务逻辑层
    └── TodoService.java
```

## API 接口

### Todo 接口 (`/api/todo`)

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| GET | `/api/todo` | 获取所有 Todo | - |
| POST | `/api/todo` | 创建 Todo | `{text, done}` |
| PUT | `/api/todo/{id}` | 更新 Todo | `{text, done}` |
| DELETE | `/api/todo/{id}` | 删除 Todo | - |
| PATCH | `/api/todo/{id}/toggle` | 切换完成状态 | - |

### 请求示例

```bash
# 获取所有 Todo
curl http://localhost:8080/api/todo

# 创建 Todo
curl -X POST http://localhost:8080/api/todo \
  -H "Content-Type: application/json" \
  -d '{"text":"学习 Spring Boot","done":false}'

# 切换状态
curl -X PATCH http://localhost:8080/api/todo/1/toggle

# 删除 Todo
curl -X DELETE http://localhost:8080/api/todo/1
```

## 开发规范

### 注解使用

项目使用 Lombok 减少样板代码：

- `@Data` - 自动生成 getter/setter/toString/equals/hashCode
- `@RequiredArgsConstructor` - 自动生成构造函数（用于依赖注入）
- `@Slf4j` - 自动生成日志对象

### 实体类规范

```java
@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "内容不能为空")
    @Size(max = 500)
    private String text;

    private boolean done;
}
```

### Repository 自定义查询

项目演示了多种 JPA 查询方式：

```java
// JPQL 查询
@Query("SELECT t FROM Todo t WHERE t.done = true")
List<Todo> findByDone();

// 原生 SQL
@Query(value = "SELECT * FROM todo WHERE done = ?1", nativeQuery = true)
List<Todo> findByDoneNative(boolean done);

// 更新操作
@Modifying
@Query("UPDATE Todo t SET t.done = :done WHERE t.id = :id")
int updateTodoStatus(@Param("id") Long id, @Param("done") boolean done);
```

### 异常处理

使用 `@RestControllerAdvice` 全局处理异常：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTodoNotFoundException(TodoNotFoundException ex) {
        // 返回 404 和错误信息
    }
}
```

### API 文档注解

使用 OpenAPI 3 注解生成 Swagger 文档：

```java
@Tag(name = "Todo管理", description = "待办事项的增删改查操作")
public class TodoController {
    @Operation(summary = "获取所有待办事项")
    @GetMapping
    public List<Todo> all() { ... }
}
```

## 前端对照概念

| 前端概念 | 后端对应 |
|---------|----------|
| Component | Controller |
| State/Store | Entity + Repository |
| fetch/axios | @GetMapping/@PostMapping |
| package.json | pom.xml |
| npm run | mvn spring-boot:run |

## Filter、Interceptor、AOP

### 执行顺序

```
HTTP 请求
    ↓
┌─────────────────────────────────────────────────────┐
│  Filter (过滤器) - Servlet 容器层                    │
│  └─ 可以拦截所有请求（包括静态资源）                   │
├─────────────────────────────────────────────────────┤
│  Interceptor (拦截器) - Spring MVC 层               │
│  └─ 只拦截 Controller 请求                          │
├─────────────────────────────────────────────────────┤
│  AOP (切面编程) - 方法层                             │
│  └─ 可以拦截任何 Spring Bean 的方法                  │
├─────────────────────────────────────────────────────┤
│  Controller → Service → Repository                  │
└─────────────────────────────────────────────────────┘
```

### 使用场景对比

| 组件 | 适用场景 | 典型用途 |
|------|---------|---------|
| **Filter** | 请求进入 Spring 之前/离开之后 | 编码转换、XSS防护、CORS、请求日志、认证令牌校验 |
| **Interceptor** | Controller 层面的请求处理 | 权限检查、登录验证、操作日志、性能监控 |
| **AOP** | 方法级别的横切关注点 | 事务管理、日志记录、性能统计、权限注解、缓存 |

### 项目中的实现

| 文件 | 类型 | 功能 |
|------|------|------|
| `filter/RequestLoggingFilter.java` | Filter | 记录所有 HTTP 请求日志 |
| `interceptor/ApiLoggingInterceptor.java` | Interceptor | 记录 API 访问日志（URL、参数、耗时） |
| `aspect/ServiceLoggingAspect.java` | AOP | Service 层方法日志和性能监控 |
| `aspect/LogExecutionTime.java` | 注解 | 自定义注解，标记需要监控执行时间的方法 |

### Filter 示例

```java
@Component
@Slf4j
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("[Filter] 请求开始 - {} {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(request, response);  // 继续执行
        log.info("[Filter] 请求结束");
    }
}
```

### Interceptor 示例

```java
@Component
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        log.info("[Interceptor] 进入 Controller: {}", request.getRequestURI());
        return true;  // true 继续，false 中断
    }
}
```

### AOP 示例

```java
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {
    @Around("execution(* com.example.demo.service.*.*(..))")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[AOP] 方法开始: {}", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        log.info("[AOP] 方法结束");
        return result;
    }
}
```

### 自定义注解使用

```java
// 在方法上添加注解，自动记录执行时间
@LogExecutionTime("创建Todo")
public Todo createTodo(Todo todo) {
    return todoRepo.save(todo);
}
```

## 注意事项

1. **数据库密码**: 生产环境请修改 `application.properties` 中的数据库密码
2. **日志级别**: 当前配置为 DEBUG 级别，生产环境建议调整为 INFO 或 WARN
3. **外部 API**: 配置了 `https://jsonplaceholder.typicode.com` 作为示例外部 API
4. **参数验证**: 使用 `@Valid` 和 `@NotBlank`/`@Size` 进行请求参数验证