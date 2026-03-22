# 架构指南：常规架构 vs 六边形架构

## 一、常规 BFF 架构

### 项目结构

```
src/main/java/com/example/demo/
├── controller/           # 控制器层：接收 HTTP 请求
│   └── UserController.java
├── service/              # 服务层：业务逻辑
│   └── UserService.java
├── repository/           # 数据访问层：数据库操作
│   └── UserRepository.java
├── model/                # 实体类：数据库映射
│   └── User.java
├── dto/                  # 数据传输对象：API 请求/响应
│   └── UserRequest.java
└── exception/            # 异常处理
    └── GlobalExceptionHandler.java
```

### 数据流向

```
HTTP 请求
    │
    ▼
┌─────────────┐
│ Controller  │  接收请求，参数校验
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Service    │  业务逻辑处理
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  数据库操作
└──────┬──────┘
       │
       ▼
   数据库
```

### 代码示例

```java
// 1. Controller - 接收请求
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }
}

// 2. Service - 业务逻辑
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(UserRequest request) {
        // 业务逻辑
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodePassword(request.getPassword()));

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return toResponse(user);
    }
}

// 3. Repository - 数据访问
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}

// 4. Model - 实体类
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
}
```

### 依赖关系

```
Controller → Service → Repository → 数据库
    │           │            │
    └───────────┴────────────┴──→ Model
```

**问题**：依赖链太长，改底层会影响上层。

---

## 二、六边形架构（端口-适配器架构）

### 核心概念

| 概念 | 作用 | 类比 |
|------|------|------|
| **Port（端口）** | 定义接口（能做什么） | 餐厅菜单 |
| **Application（应用）** | 业务逻辑（怎么做） | 厨师做菜 |
| **Adapter（适配器）** | 具体实现（用什么工具） | 采购员买菜 |

### 项目结构

```
src/main/java/com/example/
├── port/                         # 端口层：接口定义
│   ├── in/                       # 入站端口（定义业务用例）
│   │   └── UserUseCase.java
│   └── out/                      # 出站端口（定义外部依赖）
│       ├── UserRepository.java
│       └── EmailService.java
│
├── application/                  # 应用层：业务逻辑
│   └── UserService.java
│
├── adapter/                      # 适配器层：具体实现
│   ├── in/                       # 入站适配器（接收请求）
│   │   └── web/
│   │       └── UserController.java
│   └── out/                      # 出站适配器（外部服务）
│       ├── persistence/
│       │   └── UserRepositoryAdapter.java
│       └── email/
│           └── SendGridEmailAdapter.java
│
└── domain/                       # 领域层：核心实体
    └── User.java
```

### 数据流向

```
HTTP 请求
    │
    ▼
┌─────────────────┐
│ adapter/in      │  入站适配器：接收请求
│ UserController  │
└────────┬────────┘
         │ 调用
         ▼
┌─────────────────┐
│ port/in         │  入站端口：业务接口
│ UserUseCase     │
└────────┬────────┘
         │ 实现
         ▼
┌─────────────────┐
│ application     │  应用层：业务逻辑
│ UserService     │
└────────┬────────┘
         │ 调用
         ▼
┌─────────────────┐
│ port/out        │  出站端口：存储接口
│ UserRepository  │
└────────┬────────┘
         │ 实现
         ▼
┌─────────────────┐
│ adapter/out     │  出站适配器：具体实现
│ RepositoryImpl  │
└────────┬────────┘
         │
         ▼
      数据库
```

### 代码示例

```java
// ========== PORT 层：接口定义 ==========

// port/in/UserUseCase.java - 入站端口（定义业务用例）
public interface UserUseCase {
    UserResponse create(UserRequest request);
    UserResponse getById(Long id);
}

// port/out/UserRepository.java - 出站端口（定义存储接口）
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
}

// port/out/EmailService.java - 出站端口（定义邮件接口）
public interface EmailService {
    void sendWelcomeEmail(String email);
}

// ========== APPLICATION 层：业务逻辑 ==========

// application/UserService.java
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepository userRepository;  // 依赖接口
    private final EmailService emailService;      // 依赖接口

    @Override
    public UserResponse create(UserRequest request) {
        // 业务逻辑
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(user.getUsername());

        return toResponse(saved);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return toResponse(user);
    }
}

// ========== ADAPTER 层：具体实现 ==========

// adapter/in/web/UserController.java - 入站适配器
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;  // 依赖入站端口

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userUseCase.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userUseCase.getById(id);
    }
}

// adapter/out/persistence/UserRepositoryAdapter.java - 出站适配器
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;  // Spring Data JPA

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
}

// adapter/out/email/SendGridEmailAdapter.java - 出站适配器
@Component
@RequiredArgsConstructor
public class SendGridEmailAdapter implements EmailService {

    private final SendGridClient sendGridClient;

    @Override
    public void sendWelcomeEmail(String email) {
        sendGridClient.send(email, "Welcome!", "欢迎注册！");
    }
}

// ========== DOMAIN 层：核心实体 ==========

// domain/User.java
@Data
public class User {
    private Long id;
    private String username;
    private String password;
}
```

### Domain 层详解

**Domain = 核心业务实体，不依赖任何框架**

#### Domain 与 Model 的区别

| 对比项 | Model | Domain |
|--------|-------|--------|
| 位置 | `model/` | `domain/` |
| 依赖 | 依赖 JPA（@Entity、@Id） | 无依赖（纯 Java 类） |
| 用途 | 数据库映射 | 业务逻辑载体 |
| 字段 | 数据库字段 | 业务字段 + 业务方法 |

#### 代码对比

**Model（常规架构）**：

```java
// model/User.java - 依赖 JPA
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    // 只有 getter/setter，没有业务方法
}
```

**Domain（六边形架构）**：

```java
// domain/User.java - 纯 Java 类，无依赖
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private boolean active;

    // 业务方法：领域逻辑
    public void changePassword(String newPassword) {
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("密码太短");
        }
        this.password = newPassword;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}

// domain/UserRole.java - 枚举
public enum UserRole {
    USER, ADMIN, MANAGER
}
```

#### Domain 层可以包含

```
domain/
├── User.java              # 实体（Entity）
├── UserRole.java          # 枚举
├── Order.java             # 实体
├── OrderStatus.java       # 枚举
├── valueobject/           # 值对象（Value Object）
│   ├── Money.java
│   ├── Address.java
│   └── Email.java
├── event/                 # 领域事件（Domain Event）
│   └── UserRegisteredEvent.java
└── exception/             # 领域异常
    └── UserNotFoundException.java
```

#### 值对象示例

```java
// domain/valueobject/Money.java
@Data
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不同");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

// domain/valueobject/Email.java
@Data
public class Email {
    private final String value;

    public Email(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("邮箱格式错误");
        }
        this.value = value;
    }
}
```

#### 完整架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         adapter                              │
│  ┌──────────────┐                    ┌──────────────┐       │
│  │ adapter/in   │                    │ adapter/out  │       │
│  │ Controller   │                    │ Repository   │       │
│  └──────┬───────┘                    └──────┬───────┘       │
└─────────┼──────────────────────────────────┼────────────────┘
          │                                  │
          ▼                                  ▼
┌─────────────────────────────────────────────────────────────┐
│                          port                                │
│  ┌──────────────┐                    ┌──────────────┐       │
│  │  port/in     │                    │  port/out    │       │
│  │ UseCase 接口  │                    │ Repository接口│       │
│  └──────┬───────┘                    └──────┬───────┘       │
└─────────┼──────────────────────────────────┼────────────────┘
          │                                  │
          ▼                                  │
┌─────────────────────────────────────────────────────────────┐
│                       application                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                    Service                            │   │
│  │                 (业务逻辑)                             │   │
│  └──────────────────────────┬───────────────────────────┘   │
└─────────────────────────────┼───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         domain                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │    Entity    │  │ ValueObject  │  │    Event     │       │
│  │    User      │  │    Money     │  │ UserCreated  │       │
│  │   (实体)      │  │  (值对象)    │  │  (事件)      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│                                                              │
│  特点：纯 Java 类，无框架依赖，包含业务逻辑                     │
└─────────────────────────────────────────────────────────────┘
```

### 依赖关系

```
                    ┌─────────────┐
                    │   port/in   │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │ adapter/in  │ │ application │ │   测试      │
    │ Controller  │ │  Service    │ │   Mock      │
    └─────────────┘ └──────┬──────┘ └─────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  port/out   │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │adapter/out  │ │adapter/out  │ │   测试      │
    │ MySQL Impl  │ │ Mongo Impl  │ │   Mock      │
    └─────────────┘ └─────────────┘ └─────────────┘
```

**优点**：所有层都依赖 port（接口），不依赖具体实现。

---

## 三、两种架构对应关系

| 常规架构 | 六边形架构 | 作用 |
|----------|-----------|------|
| `controller/` | `adapter/in/` | 接收 HTTP 请求 |
| `service/` | `application/` | 业务逻辑 |
| `repository/` | `adapter/out/` | 数据访问实现 |
| `model/` | `domain/` | 实体类 |
| `dto/` | `dto/` | 数据传输对象 |
| ❌ 没有 | `port/in/` | 业务接口定义 |
| ❌ 没有 | `port/out/` | 存储接口定义 |

### 对比图

```
常规架构：
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Controller  │────▶│  Service    │────▶│ Repository  │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │                   │
       └───────────────────┴───────────────────┘
                           │
                           ▼
                      直接依赖具体类

六边形架构：
                           ┌─────────────┐
                           │   port/in   │
                           └──────┬──────┘
                                  │
       ┌──────────────────────────┼──────────────────────────┐
       │                          │                          │
       ▼                          ▼                          ▼
┌─────────────┐           ┌─────────────┐           ┌─────────────┐
│ adapter/in  │           │ application │           │    测试     │
└─────────────┘           └──────┬──────┘           └─────────────┘
                                 │
                                 ▼
                          ┌─────────────┐
                          │  port/out   │
                          └──────┬──────┘
                                 │
       ┌─────────────────────────┼─────────────────────────┐
       │                         │                         │
       ▼                         ▼                         ▼
┌─────────────┐          ┌─────────────┐          ┌─────────────┐
│adapter/out  │          │adapter/out  │          │    测试     │
│   MySQL     │          │   Mongo     │          │    Mock     │
└─────────────┘          └─────────────┘          └─────────────┘
                                 │
                                 ▼
                           依赖接口，不依赖实现
```

---

## 四、适用场景

### 常规架构适合

- ✅ 小型项目、初创项目
- ✅ 团队规模小（1-5人）
- ✅ 业务逻辑简单
- ✅ 不需要切换数据库/API
- ✅ 快速迭代开发

### 六边形架构适合

- ✅ 大型项目、企业级应用
- ✅ 团队规模大（10人以上）
- ✅ 业务逻辑复杂
- ✅ 需要切换数据库（MySQL → PostgreSQL）
- ✅ 需要切换第三方服务（阿里云 → AWS）
- ✅ 需要高测试覆盖率
- ✅ 长期维护项目

---

## 五、实际案例

### 案例1：切换数据库

**常规架构**：
```java
// Service 直接用 JPA，换数据库要改 Service
@Service
public class UserService {
    private UserRepository repository;  // JPA Repository

    public void save(User user) {
        repository.save(user);  // 换 MongoDB 要改这里
    }
}
```

**六边形架构**：
```java
// Service 只依赖接口，换数据库只改 Adapter
@Service
public class UserService {
    private UserRepository repository;  // 接口

    public void save(User user) {
        repository.save(user);  // 不用改
    }
}

// 只需要换 Adapter 实现
@Repository
public class MySQLUserRepository implements UserRepository { ... }
@Repository
public class MongoUserRepository implements UserRepository { ... }
```

### 案例2：切换第三方服务

**场景**：从阿里云短信切换到腾讯云短信

```java
// port/out/SmsService.java（接口不变）
public interface SmsService {
    void send(String phone, String message);
}

// adapter/out/AliyunSmsAdapter.java（旧实现）
@Component
public class AliyunSmsAdapter implements SmsService {
    public void send(String phone, String message) {
        // 阿里云 SDK
    }
}

// adapter/out/TencentSmsAdapter.java（新实现）
@Component
public class TencentSmsAdapter implements SmsService {
    public void send(String phone, String message) {
        // 腾讯云 SDK
    }
}

// 业务代码完全不用改！
```

### 案例3：单元测试

**常规架构**：
```java
@SpringBootTest  // 需要启动整个 Spring 容器
class UserServiceTest {
    @MockBean
    private UserRepository repository;  // Mock 具体类

    @Test
    void testCreate() {
        // ...
    }
}
```

**六边形架构**：
```java
class UserServiceTest {
    private UserRepository repository = mock(UserRepository.class);  // Mock 接口
    private EmailService emailService = mock(EmailService.class);
    private UserService service = new UserService(repository, emailService);

    @Test
    void testCreate() {
        // 纯单元测试，不需要 Spring 容器
    }
}
```

---

## 六、总结

### 各层职责一览

| 层 | 一句话 | 类比 |
|---|--------|------|
| **domain** | 核心业务实体，无框架依赖，包含业务方法 | 餐厅的食材 |
| **port** | 接口定义（能做什么） | 餐厅菜单 |
| **application** | 业务逻辑（怎么做） | 厨师做菜 |
| **adapter** | 具体实现（用什么工具） | 采购员买菜 |

### 架构对比

| 对比项 | 常规架构 | 六边形架构 |
|--------|----------|-----------|
| **复杂度** | 简单 | 较复杂 |
| **代码量** | 少 | 多（多了 port 层） |
| **依赖方向** | 单向依赖 | 依赖接口 |
| **可测试性** | 需要 Mock 具体类 | Mock 接口即可 |
| **可扩展性** | 一般 | 好 |
| **学习曲线** | 低 | 较高 |
| **适合项目** | 小型项目 | 大型项目 |

**建议**：
- 初创项目、学习项目 → 常规架构
- 企业级项目、长期维护项目 → 六边形架构

---

## 七、参考资料

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Boot Hexagonal Architecture](https://www.baeldung.com/hexagonal-architecture-ddd-spring)