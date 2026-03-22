# JWT 认证指南

## 什么是 JWT？

JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在各方之间安全传输信息的紧凑、URL 安全的方式。

### JWT 结构

JWT 由三部分组成，用 `.` 分隔：

```
Header.Payload.Signature
```

**示例：**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwODY0MDB9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

| 部分 | 说明 | 示例内容 |
|------|------|----------|
| Header | 算法和类型 | `{"alg":"HS256","typ":"JWT"}` |
| Payload | 用户数据 | `{"sub":"admin","role":"USER","exp":1700086400}` |
| Signature | 签名 | 防止篡改 |

### JWT vs Session

| 对比项 | JWT | Session |
|--------|-----|---------|
| 存储位置 | 客户端 | 服务端 |
| 扩展性 | 无状态，易扩展 | 需要共享 Session |
| 跨域 | 天然支持 | 需要额外配置 |
| 安全性 | Token 泄露风险 | Session 劫持风险 |
| 注销 | 较难立即失效 | 立即失效 |

---

## 项目实现

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端                                │
│  1. POST /api/auth/login {username, password}               │
│  2. 收到 {token: "xxx"}                                      │
│  3. GET /api/todo Header: Authorization: Bearer xxx         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      服务端                                   │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ AuthController │──▶│ UserService │──▶│ JwtService │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                              │                              │
│                              ▼                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              JwtAuthenticationFilter                  │  │
│  │  1. 提取 Authorization Header                         │  │
│  │  2. 验证 Token                                        │  │
│  │  3. 设置 SecurityContext                              │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 文件结构

```
src/main/java/com/example/demo/
├── config/
│   └── SecurityConfig.java          # Spring Security 配置
├── controller/
│   └── AuthController.java          # 认证接口（登录/注册）
├── dto/
│   ├── LoginRequest.java            # 登录请求 DTO
│   ├── RegisterRequest.java         # 注册请求 DTO
│   └── AuthResponse.java            # 认证响应 DTO
├── model/
│   └── User.java                    # 用户实体
├── repository/
│   └── UserRepository.java          # 用户数据访问
├── service/
│   └── UserService.java             # 用户业务逻辑
└── security/
    ├── JwtService.java              # JWT 生成/验证
    ├── JwtAuthenticationFilter.java # JWT 过滤器
    └── UserDetailsServiceImpl.java  # 用户详情服务
```

---

## 核心组件

### 1. JwtService - JWT 工具类

负责 Token 的生成、解析和验证。

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 生成 Token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 提取用户名
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 验证 Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }
}
```

### 2. JwtAuthenticationFilter - JWT 过滤器

拦截每个请求，验证 Token。

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // 1. 从 Header 提取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 2. 验证 Token 并设置认证信息
        String username = jwtService.extractUsername(token);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(token, user)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

### 3. SecurityConfig - 安全配置

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)  // 禁用 CSRF
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // 公开接口
                .anyRequest().authenticated()  // 其他需要认证
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

## API 接口

### 认证接口

| 方法 | 路径 | 功能 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | ❌ |
| POST | `/api/auth/login` | 用户登录 | ❌ |

### 业务接口

| 方法 | 路径 | 功能 | 认证 |
|------|------|------|------|
| GET | `/api/todo` | 获取所有 Todo | ✅ |
| POST | `/api/todo` | 创建 Todo | ✅ |
| PUT | `/api/todo/{id}` | 更新 Todo | ✅ |
| DELETE | `/api/todo/{id}` | 删除 Todo | ✅ |

---

## 使用示例

### 1. 注册用户

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "role": "USER"
  }'
```

**响应：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "USER"
}
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**响应：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "USER"
}
```

### 3. 访问受保护接口

```bash
# 不带 Token - 403 Forbidden
curl http://localhost:8080/api/todo

# 带 Token - 成功
curl http://localhost:8080/api/todo \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. 创建 Todo

```bash
curl -X POST http://localhost:8080/api/todo \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{"text": "学习 JWT", "done": false}'
```

---

## 前端集成

### 存储 Token

```javascript
// 登录后存储
localStorage.setItem('token', response.token);

// 获取 Token
const token = localStorage.getItem('token');

// 退出登录
localStorage.removeItem('token');
```

### 请求拦截器（Axios）

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// 请求拦截器 - 自动添加 Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器 - 处理 401
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 使用示例

```javascript
// 登录
async function login(username, password) {
  const response = await api.post('/auth/login', { username, password });
  localStorage.setItem('token', response.data.token);
  return response.data;
}

// 获取 Todo 列表
async function getTodos() {
  const response = await api.get('/todo');
  return response.data;
}
```

---

## 配置说明

### application.properties

```properties
# JWT 密钥（生产环境应使用环境变量）
jwt.secret=your-very-long-secret-key-that-should-be-at-least-256-bits-long

# Token 过期时间（毫秒），默认 24 小时
jwt.expiration=86400000
```

### 生产环境建议

```properties
# 使用环境变量
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

---

## 安全注意事项

### 1. 密钥管理

```bash
# 生成安全的密钥（至少 256 位）
openssl rand -base64 32
```

### 2. HTTPS

生产环境必须使用 HTTPS，防止 Token 被窃取。

### 3. Token 过期

- 设置合理的过期时间
- 可实现 Refresh Token 机制

### 4. 敏感操作

对于敏感操作（修改密码、删除账户），应要求重新验证密码。

---

## 常见问题

### Q: Token 过期了怎么办？

A: 重新调用登录接口获取新 Token。

### Q: 如何实现 Token 刷新？

A: 可以实现 Refresh Token 机制：

```java
// 生成 Refresh Token（有效期更长）
public String generateRefreshToken(String username) {
    return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7天
            .signWith(getSigningKey())
            .compact();
}
```

### Q: 如何实现登出？

A: JWT 是无状态的，无法服务端主动失效。解决方案：
1. 客户端删除 Token
2. 使用 Token 黑名单（Redis）
3. 设置较短的过期时间

---

## 扩展阅读

- [JWT.io](https://jwt.io/) - JWT 在线调试工具
- [RFC 7519](https://tools.ietf.org/html/rfc7519) - JWT 规范
- [Spring Security 文档](https://docs.spring.io/spring-security/reference/)