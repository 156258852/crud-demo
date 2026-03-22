# Spring Boot 接口编程项目结构

## 项目概述

本项目已改造为**接口编程**模式，采用**接口 + 实现类**的设计方式，提高代码的可维护性、可测试性和可扩展性。

## 项目结构

```
src/main/java/com/example/demo/
├── DemoApplication.java              # 应用启动类
│
├── aspect/                           # AOP 切面
│   ├── LogExecutionTime.java         # 自定义执行时间注解
│   └── ServiceLoggingAspect.java     # Service 层日志切面
│
├── client/                           # 外部 API 客户端
│   ├── ExternalApiClient.java        # 外部 API 客户端接口
│   └── impl/
│       └── ExternalApiClientImpl.java # 外部 API 客户端实现
│
├── config/                           # 配置类
│   ├── SecurityConfig.java           # Spring Security 配置
│   ├── SwaggerConfig.java            # Swagger/OpenAPI 配置
│   ├── WebClientConfig.java          # WebClient 配置
│   └── WebConfig.java                # Web MVC 配置
│
├── controller/                       # REST 控制器
│   ├── AuthController.java           # 认证控制器
│   ├── ExternalApiController.java    # 外部 API 控制器
│   └── TodoController.java           # Todo CRUD 控制器
│
├── dto/                              # 数据传输对象
│   ├── AuthResponse.java             # 认证响应
│   ├── CreateTodoRequest.java        # 创建 Todo 请求
│   ├── LoginRequest.java             # 登录请求
│   ├── RegisterRequest.java          # 注册请求
│   ├── TodoResponse.java             # Todo 响应
│   └── UpdateTodoRequest.java        # 更新 Todo 请求
│
├── exception/                        # 异常处理
│   ├── GlobalExceptionHandler.java   # 全局异常处理器
│   └── TodoNotFoundException.java    # Todo 未找到异常
│
├── filter/                           # 过滤器
│   └── RequestLoggingFilter.java     # 请求日志过滤器
│
├── interceptor/                      # 拦截器
│   └── ApiLoggingInterceptor.java    # API 日志拦截器
│
├── model/                            # 实体类
│   ├── Todo.java                     # Todo 实体
│   └── User.java                     # 用户实体
│
├── repository/                       # 数据访问层（接口）
│   ├── TodoRepository.java           # Todo 数据访问接口
│   └── UserRepository.java           # 用户数据访问接口
│
├── response/                         # 统一响应
│   └── ApiResponse.java              # 统一 API 响应
│
├── security/                         # 安全模块
│   ├── CustomUserDetailsService.java # 用户详情服务接口
│   ├── JwtAuthenticationFilter.java  # JWT 认证过滤器
│   ├── JwtService.java               # JWT 服务接口
│   └── impl/
│       ├── CustomUserDetailsServiceImpl.java # 用户详情服务实现
│       └── JwtServiceImpl.java               # JWT 服务实现
│
└── service/                          # 业务逻辑层
    ├── TodoService.java              # Todo 服务接口
    ├── UserService.java              # 用户服务接口
    └── impl/
        ├── TodoServiceImpl.java      # Todo 服务实现
        └── UserServiceImpl.java      # 用户服务实现
```

## 接口编程设计

### 1. Service 层

| 接口 | 实现类 | 功能 |
|------|--------|------|
| `TodoService` | `TodoServiceImpl` | Todo 增删改查业务逻辑 |
| `UserService` | `UserServiceImpl` | 用户注册、登录业务逻辑 |
| `JwtService` | `JwtServiceImpl` | JWT Token 生成、解析、验证 |
| `CustomUserDetailsService` | `CustomUserDetailsServiceImpl` | Spring Security 用户加载 |

### 2. Client 层

| 接口 | 实现类 | 功能 |
|------|--------|------|
| `ExternalApiClient` | `ExternalApiClientImpl` | 外部 API 调用（WebClient） |

### 3. Repository 层

| 接口 | 功能 |
|------|------|
| `TodoRepository` | Todo 数据访问（JPA） |
| `UserRepository` | 用户数据访问（JPA） |

## 架构分层

```
┌─────────────────────────────────────────────────────────┐
│                    Controller 层                         │
│  (依赖 Service 接口，不直接依赖实现类)                    │
├─────────────────────────────────────────────────────────┤
│                    Service 层                            │
│  接口 (service/) + 实现 (service/impl/)                 │
├─────────────────────────────────────────────────────────┤
│                   Repository 层                          │
│  JPA Repository 接口                                     │
├─────────────────────────────────────────────────────────┤
│                    Model 层                              │
│  实体类（Entity）                                        │
└─────────────────────────────────────────────────────────┘
```

## 依赖注入

所有 Controller 依赖 Service **接口**，而非实现类：

```java
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;  // 依赖接口
}
```

Spring 自动注入实现类（通过 `@Service` 注解）：

```java
@Service
public class TodoServiceImpl implements TodoService {
    // 实现细节
}
```

## 优势

1. **松耦合**: Controller 不依赖具体实现，便于替换实现
2. **易测试**: 可以轻松使用 Mock 对象进行单元测试
3. **可扩展**: 新增实现无需修改调用方代码
4. **清晰分层**: 接口定义清晰，职责分明

## 运行项目

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 打包
mvn clean package -DskipTests

# 运行测试
mvn test
```

## API 访问

| 服务 | 地址 |
|------|------|
| 应用首页 | http://localhost:8080 |
| API 文档 (Swagger) | http://localhost:8080/swagger-ui.html |
| API Docs JSON | http://localhost:8080/v3/api-docs |
