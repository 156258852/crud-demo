# Filter、Interceptor、AOP 使用指南

## 一、概述

Spring Boot 中有三种常用的请求处理机制：Filter（过滤器）、Interceptor（拦截器）和 AOP（面向切面编程）。它们在不同的层次上拦截和处理请求，各有适用场景。

## 二、执行顺序

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
    ↓
HTTP 响应
```

## 三、使用场景对比

| 组件 | 层级 | 适用场景 | 典型用途 |
|------|------|---------|---------|
| **Filter** | Servlet 容器 | 请求进入 Spring 之前 | 编码转换、XSS防护、CORS、JWT验证 |
| **Interceptor** | Spring MVC | Controller 层面 | 权限检查、登录验证、操作日志 |
| **AOP** | 方法层 | 方法级别的横切关注点 | 事务管理、日志记录、性能监控 |

### 选择建议

| 场景 | 推荐方案 |
|------|---------|
| 需要拦截静态资源 | Filter |
| JWT Token 验证 | Filter 或 Interceptor |
| 记录 API 访问日志 | Interceptor |
| 统计方法执行时间 | AOP |
| 自定义注解功能 | AOP |
| 请求参数解密/响应加密 | Filter 或 Interceptor |

## 四、Filter（过滤器）

### 4.1 简介

Filter 是 Servlet 规范的一部分，在请求进入 Spring 容器之前执行。它可以拦截所有请求，包括静态资源。

### 4.2 实现方式

```java
@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        long startTime = System.currentTimeMillis();
        
        // 请求前处理
        log.info("[Filter] 请求开始 - {} {}", req.getMethod(), req.getRequestURI());
        
        try {
            // 继续执行过滤器链
            chain.doFilter(request, response);
        } finally {
            // 请求后处理
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Filter] 请求结束 - 耗时: {}ms", duration);
        }
    }
}
```

### 4.3 Filter 配置（可选）

如果需要更多控制，可以通过 `FilterRegistrationBean` 配置：

```java
@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestLoggingFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);  // 执行顺序
        return registration;
    }
}
```

### 4.4 多个 Filter 的执行顺序

使用 `@Order` 注解或 `FilterRegistrationBean.setOrder()` 控制顺序：

```java
@Component
@Order(1)  // 数字越小越先执行
public class FirstFilter implements Filter { ... }

@Component
@Order(2)
public class SecondFilter implements Filter { ... }
```

## 五、Interceptor（拦截器）

### 5.1 简介

Interceptor 是 Spring MVC 的一部分，在 Controller 执行前后处理。它只能拦截 Controller 请求，不能拦截静态资源。

### 5.2 实现方式

```java
@Component
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

    /**
     * Controller 执行前
     * 返回 true 继续执行，false 则中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        log.info("[Interceptor] preHandle - {}", request.getRequestURI());
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    /**
     * Controller 执行后，视图渲染前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        log.info("[Interceptor] postHandle");
    }

    /**
     * 视图渲染后（请求完成后）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        log.info("[Interceptor] afterCompletion - 耗时: {}ms", duration);
    }
}
```

### 5.3 注册拦截器

```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiLoggingInterceptor apiLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/api/**")           // 拦截路径
                .excludePathPatterns(                 // 排除路径
                    "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                );
    }
}
```

### 5.4 多个 Interceptor 的执行顺序

实现 `Ordered` 接口或使用 `order()` 方法：

```java
registry.addInterceptor(new FirstInterceptor()).order(1);
registry.addInterceptor(new SecondInterceptor()).order(2);
```

## 六、AOP（面向切面编程）

### 6.1 简介

AOP 用于处理方法级别的横切关注点，如日志、事务、权限等。它可以拦截任何 Spring Bean 的方法。

### 6.2 核心概念

| 概念 | 说明 |
|------|------|
| **Aspect（切面）** | 横切关注点的模块化，如日志切面 |
| **JoinPoint（连接点）** | 程序执行的某个点，通常是方法调用 |
| **Pointcut（切点）** | 定义在哪些连接点上执行通知 |
| **Advice（通知）** | 在切点上执行的操作 |
| **Weaving（织入）** | 将切面应用到目标对象的过程 |

### 6.3 通知类型

| 通知类型 | 说明 |
|---------|------|
| `@Before` | 方法执行前 |
| `@After` | 方法执行后（无论成功或异常） |
| `@AfterReturning` | 方法成功返回后 |
| `@AfterThrowing` | 方法抛出异常后 |
| `@Around` | 环绕通知，最强大，可控制方法执行 |

### 6.4 实现方式

#### 6.4.1 基于切点表达式

```java
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    /**
     * 切点：匹配 Service 层所有方法
     */
    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void serviceLayer() {}

    /**
     * 环绕通知
     */
    @Around("serviceLayer()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        
        log.info("[AOP] 方法开始: {}", methodName);
        
        try {
            Object result = joinPoint.proceed();  // 执行原方法
            log.info("[AOP] 方法结束: {}", methodName);
            return result;
        } catch (Exception e) {
            log.error("[AOP] 方法异常: {} - {}", methodName, e.getMessage());
            throw e;
        }
    }
}
```

#### 6.4.2 基于自定义注解

**定义注解：**

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    String value() default "";
}
```

**使用注解：**

```java
@Service
public class TodoService {
    
    @LogExecutionTime("创建Todo")
    public Todo createTodo(Todo todo) {
        return todoRepo.save(todo);
    }
}
```

**切面处理：**

```java
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, 
                                   LogExecutionTime logExecutionTime) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String description = logExecutionTime.value();
        
        long startTime = System.currentTimeMillis();
        
        log.info("[AOP] 开始执行: {}", description);
        
        Object result = joinPoint.proceed();
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[AOP] 执行完成: {} - 耗时: {}ms", description, duration);
        
        return result;
    }
}
```

### 6.5 常用切点表达式

| 表达式 | 说明 |
|--------|------|
| `execution(* com.example.demo.service.*.*(..))` | Service 层所有方法 |
| `execution(* com.example.demo..*(..))` | demo 包及子包下所有方法 |
| `@annotation(com.example.demo.aspect.LogExecutionTime)` | 带有特定注解的方法 |
| `within(com.example.demo.service.*)` | Service 包下的所有方法 |
| `bean(todoService)` | 名为 todoService 的 Bean 的所有方法 |

## 七、日志输出示例

启动项目后，调用 API 可以看到完整的执行日志：

```bash
curl http://localhost:8080/api/todo
```

**日志输出：**

```
[Filter] 请求开始 - GET /api/todo 来自 0:0:0:0:0:0:0:1
[Interceptor] preHandle - GET /api/todo
[AOP] 方法开始 - TodoService.getAllTodos() 参数: []
[AOP] 方法结束 - TodoService.getAllTodos() 耗时: 25ms
[Interceptor] postHandle - Controller 执行完毕，准备渲染视图
[Interceptor] afterCompletion - 请求完成 - 状态码: 200 - 耗时: 55ms
[Filter] 请求结束 - GET /api/todo - 耗时: 60ms - 状态码: 200
```

## 八、项目文件结构

```
src/main/java/com/example/demo/
├── aspect/                           # AOP 切面
│   ├── LogExecutionTime.java         # 自定义注解
│   └── ServiceLoggingAspect.java     # Service 层日志切面
├── config/
│   └── WebConfig.java                # 拦截器配置
├── filter/
│   └── RequestLoggingFilter.java     # 请求日志过滤器
└── interceptor/
    └── ApiLoggingInterceptor.java    # API 日志拦截器
```

## 九、常见问题

### Q1: Filter 和 Interceptor 有什么区别？

| 对比项 | Filter | Interceptor |
|--------|--------|-------------|
| 所属 | Servlet 规范 | Spring MVC |
| 拦截范围 | 所有请求（包括静态资源） | 只拦截 Controller |
| 获取 Bean | 需要额外配置 | 可以直接注入 |
| 执行时机 | 在 DispatcherServlet 之前 | 在 Controller 之前 |

### Q2: 什么时候用 AOP？

- 需要在多个方法上添加相同逻辑时
- 需要自定义注解实现功能时
- 需要统计方法执行时间时
- 需要统一处理异常时

### Q3: 如何控制多个 Filter/Interceptor 的执行顺序？

- Filter: 使用 `@Order` 注解或 `FilterRegistrationBean.setOrder()`
- Interceptor: 使用 `order()` 方法

### Q4: AOP 不生效怎么办？

检查以下几点：
1. 目标类是否是 Spring Bean（有 `@Service`、`@Component` 等注解）
2. 方法是否是 public
3. 是否在同一个类内部调用（内部调用不会触发 AOP）
4. 是否添加了 `@EnableAspectJAutoProxy`（Spring Boot 默认开启）