# 单元测试编写指南

本文档介绍 Spring Boot 项目中单元测试的最佳实践、注意事项以及哪些代码需要编写测试。

## 目录

1. [测试分类](#测试分类)
2. [哪些代码需要测试](#哪些代码需要测试)
3. [Stub vs Mock 框架](#stub-vs-mock-框架)
4. [分层测试策略](#分层测试策略)
5. [测试命名规范](#测试命名规范)
6. [常用断言方法](#常用断言方法)
7. [注意事项](#注意事项)
8. [测试示例](#测试示例)

---

## 测试分类

### 单元测试 (Unit Test)

**定义**: 测试单个类或方法的行为，依赖项使用 Stub 或 Mock 模拟。

**特点**:
- 执行速度快（毫秒级）
- 不依赖外部资源（数据库、网络）
- 隔离测试，失败时容易定位问题

### 集成测试 (Integration Test)

**定义**: 测试多个组件协作的行为，使用真实依赖或测试替身。

**特点**:
- 执行速度较慢
- 可能依赖内存数据库（H2）
- 更接近真实运行环境

---

## 哪些代码需要测试

### ✅ 必须测试

| 层级 | 原因 | 测试类型 |
|------|------|----------|
| **Service 层** | 包含核心业务逻辑 | 单元测试 |
| **工具类** | 纯逻辑，无依赖 | 单元测试 |
| **算法/计算逻辑** | 边界条件和异常情况多 | 单元测试 |
| **数据验证逻辑** | 需验证各种输入情况 | 单元测试 |
| **自定义 Repository 方法** | 验证 JPQL/SQL 正确性 | 集成测试 |

### ⚠️ 视情况测试

| 类型 | 建议 |
|------|------|
| **Controller 层** | 如有复杂逻辑可测试；如只是简单转发，可跳过 |
| **异常处理** | 测试异常场景和错误消息 |
| **数据转换逻辑** | 如有复杂映射规则需测试 |

### ❌ 可不测试

| 类型 | 原因 |
|------|------|
| **Getter/Setter** | 框架生成或 Lombok 生成，无业务逻辑 |
| **简单 POJO** | 只有属性，无逻辑 |
| **框架配置类** | Spring 自动管理，配置正确即可 |
| **标准 CRUD** | JpaRepository 提供的方法已由框架保证 |

---

## Stub vs Mock 框架

### Stub（桩对象）

**定义**: 手动实现的测试替身，包含预设的行为和数据。

**优点**:
- 不依赖第三方库
- 行为清晰可见
- 易于理解和调试

**缺点**:
- 需要手动编写
- 复杂场景工作量大

**示例**（本项目使用）:
```java
public class TodoRepoStub implements TodoRepo {
    private final Map<Long, Todo> dataStore = new HashMap<>();
    
    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(dataStore.values());
    }
    
    // 添加验证辅助方法
    public int getSaveCallCount() {
        return saveCallCount;
    }
}
```

### Mock 框架（如 Mockito）

**优点**:
- 快速创建测试替身
- 丰富的 API
- 与 Spring Boot 深度集成

**缺点**:
- 需要学习框架 API
- 过度使用导致测试代码难懂
- 可能隐藏真实问题

**选择建议**:
- 学习阶段：使用 Stub，理解测试本质
- 生产项目：简单场景用 Stub，复杂场景用 Mockito

---

## 分层测试策略

### 1. Service 层测试（单元测试）

**目标**: 验证业务逻辑正确性

**方法**: 使用 Stub 模拟 Repository

```java
class TodoServiceTest {
    private TodoRepoStub repoStub;  // Stub
    private TodoService service;

    @BeforeEach
    void setUp() {
        repoStub = new TodoRepoStub();
        service = new TodoService(repoStub);
    }

    @Test
    void createTodo_ShouldSetDefaultDoneToFalse() {
        // Given
        Todo input = new Todo();
        input.setText("新任务");
        input.setDone(true);  // 即使传入 true

        // When
        Todo result = service.createTodo(input);

        // Then
        assertFalse(result.isDone());  // 应被重置为 false
    }
}
```

### 2. Controller 层测试

**两种方式**:

#### 方式一：纯单元测试（本项目采用）

直接实例化 Controller，使用 Service Stub：

```java
class TodoControllerTest {
    private TodoServiceStub serviceStub;
    private TodoController controller;

    @BeforeEach
    void setUp() {
        serviceStub = new TodoServiceStub();
        controller = new TodoController(serviceStub);
    }

    @Test
    void all_ShouldReturnTodoList() {
        serviceStub.addPresetData(1L, "任务1", false);
        
        List<Todo> result = controller.all();
        
        assertEquals(1, result.size());
    }
}
```

#### 方式二：WebMvc 测试（可选）

使用 `@WebMvcTest` 测试 HTTP 层：

```java
@WebMvcTest(TodoController.class)
class TodoControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void getAllTodos_ShouldReturnJson() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(...));

        mockMvc.perform(get("/api/todo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].text").value("任务1"));
    }
}
```

### 3. Repository 层测试（集成测试）

**目标**: 验证 JPA 映射和自定义查询

**方法**: 使用 `@DataJpaTest` + H2 内存数据库

```java
@DataJpaTest
@ActiveProfiles("test")
class TodoRepoTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoRepo todoRepo;

    @Test
    void findByDone_ShouldReturnCompletedTodos() {
        // Given
        entityManager.persist(createTodo("未完成", false));
        entityManager.persist(createTodo("已完成", true));

        // When
        List<Todo> result = todoRepo.findByDone();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isDone());
    }
}
```

---

## 测试命名规范

### 方法命名模式

推荐使用 `方法名_场景_预期结果` 格式：

```java
// ✅ 推荐
void findById_Found()
void findById_NotFound()
void createTodo_Success()
void createTodo_DefaultDoneFalse()
void updateTodo_NotFound_ThrowsException()

// ❌ 不推荐
void testFindById()
void test1()
void shouldWork()
```

### @DisplayName 使用

使用中文描述，让测试报告更易读：

```java
@Test
@DisplayName("创建待办事项 - 验证默认状态为未完成")
void createTodo_DefaultDoneFalse() {
    // ...
}
```

### Given-When-Then 结构

测试方法内部使用注释划分三个阶段：

```java
@Test
void updateTodo_Success() {
    // Given - 准备测试数据
    Todo existing = createTodo(1L, "原始内容", false);
    repoStub.addPresetData(1L, "原始内容", false);
    
    Todo updateData = new Todo();
    updateData.setText("新内容");

    // When - 执行测试方法
    Todo result = service.updateTodo(1L, updateData);

    // Then - 验证结果
    assertEquals("新内容", result.getText());
}
```

---

## 常用断言方法

JUnit 5 提供的断言（`org.junit.jupiter.api.Assertions`）：

```java
// 基本断言
assertEquals(expected, actual);
assertNotEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(value);
assertNotNull(value);

// 数组/集合断言
assertArrayEquals(expectedArray, actualArray);
assertIterableEquals(expectedList, actualList);

// 异常断言
assertThrows(ExceptionClass.class, () -> {
    // 会抛异常的代码
});

// 组合断言
assertAll(
    () -> assertEquals("任务1", result.get(0).getText()),
    () -> assertTrue(result.get(0).isDone())
);

// 超时断言
assertTimeout(Duration.ofSeconds(1), () -> {
    // 执行代码
});
```

---

## 注意事项

### 1. 测试隔离

每个测试方法应该独立运行，互不影响：

```java
@BeforeEach
void setUp() {
    stub.clear();  // 清空之前的数据
}
```

### 2. 测试边界条件

不仅要测试正常情况，还要测试：

- 空值/null
- 空集合
- 边界值（最大、最小）
- 异常情况

```java
@Test
void findByTextContaining_EmptyKeyword() {
    // 测试空关键词
}

@Test
void findByTextContaining_SpecialCharacters() {
    // 测试特殊字符
}
```

### 3. 验证方法调用

使用 Stub 的计数器验证方法是否被调用：

```java
@Test
void deleteTodo_ShouldCallRepository() {
    // When
    service.deleteTodo(1L);

    // Then
    assertEquals(1, stub.getDeleteByIdCallCount());
    assertEquals(1L, stub.getLastDeletedId());
}
```

### 4. 不要测试私有方法

私有方法是实现细节，应通过公共方法间接测试：

```java
// ❌ 不要使用反射测试私有方法
// ✅ 测试调用私有方法的公共方法
```

### 5. 避免测试实现细节

测试行为而非实现：

```java
// ❌ 测试实现细节
@Test
void save_ShouldUseHashMap() {
    // ...
}

// ✅ 测试行为
@Test
void save_ShouldPersistAndReturnTodo() {
    Todo saved = service.saveTodo(todo);
    assertNotNull(saved.getId());
}
```

### 6. 测试覆盖率

目标是覆盖核心业务逻辑，而非追求 100%：

- 核心业务逻辑：> 80%
- 工具类：> 90%
- 整体项目：> 70%

运行测试覆盖率：

```bash
mvn jacoco:report
```

---

## 测试示例总结

### 本项目测试结构

```
src/test/
├── java/com/example/demo/
│   ├── stub/
│   │   ├── TodoRepoStub.java      # Repository Stub
│   │   └── TodoServiceStub.java   # Service Stub
│   ├── service/
│   │   └── TodoServiceTest.java   # Service 单元测试
│   ├── controller/
│   │   └── TodoControllerTest.java # Controller 单元测试
│   └── repository/
│       └── TodoRepoTest.java      # Repository 集成测试
└── resources/
    └── application-test.properties # 测试环境配置
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=TodoServiceTest

# 运行单个测试方法
mvn test -Dtest=TodoServiceTest#createTodo_Success

# 跳过测试
mvn package -DskipTests
```

---

## 快速参考卡

| 场景 | 测试类型 | 使用技术 |
|------|----------|----------|
| Service 业务逻辑 | 单元测试 | Stub + JUnit 5 |
| Controller HTTP | 单元测试 | Stub / @WebMvcTest |
| Repository 自定义查询 | 集成测试 | @DataJpaTest + H2 |
| 全流程验证 | 集成测试 | @SpringBootTest |

| 测试原则 | 说明 |
|----------|------|
| FIRST | Fast, Independent, Repeatable, Self-validating, Timely |
| AAA | Arrange, Act, Assert |
| Given-When-Then | 准备、执行、验证 |