# 前端开发者的Spring Boot后端入门指南

## 什么是BFF（Backend for Frontend）？
BFF模式是指专门为前端开发的后端服务，它负责处理前端需要的数据和业务逻辑，让前端开发者更专注于用户体验和界面开发。

## 项目概述
这是一个简单的Todo CRUD应用，采用Spring Boot构建，为前端提供REST API接口。

## 技术栈对比（前端 vs 后端）

| 前端概念 | 后端对应概念 | 示例 |
|---------|-------------|------|
| Component | Controller | TodoController.java |
| State Management | Entity/Model | Todo.java |
| API Calls | Repository | TodoRepo.java |
| App Entry | Main Class | DemoApplication.java |

## 核心文件解析

### 1. 控制器 (相当于前端的路由处理器)
```java
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoRepo repo;

    // GET /api/todo - 获取所有todos (类似前端的GET请求)
    @GetMapping
    public List<Todo> all() { return repo.findAll(); }

    // POST /api/todo - 创建新todo (类似前端的POST请求)
    @PostMapping
    public Todo create(@RequestBody Todo todo) { return repo.save(todo); }

    // DELETE /api/todo/{id} - 删除todo
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
```

#### 注解详解

**类级别注解：**
- `@RestController`：标识此类为控制器，所有方法返回JSON数据
- `@RequestMapping("/api/todo")`：定义基础URL路径，所有方法都会加上此前缀
- `@RequiredArgsConstructor`：Lombok注解，自动生成依赖注入构造函数

**方法级别注解：**
- `@GetMapping`：处理HTTP GET请求（获取数据）
- `@PostMapping`：处理HTTP POST请求（创建数据）
- `@DeleteMapping("/{id}")`：处理HTTP DELETE请求（删除数据）

**参数级别注解：**
- `@RequestBody`：将请求体JSON数据转换为Java对象
- `@PathVariable`：从URL路径中提取参数值

### 2. 数据模型 (相当于前端的TypeScript接口)
```java
@Entity
public class Todo {
    private Long id;      // 对应前端的 { id: number }
    private String text;  // 对应前端的 { text: string }
    private boolean done; // 对应前端的 { done: boolean }
}
```

#### 注解详解

- `@Entity`：标识此类为JPA实体，对应数据库表

**数据库表自动生成说明：**

虽然我们只定义了实体类，但Spring Boot会根据以下配置自动创建数据库表：

1. `application.properties` 中的配置：
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

2. 当应用启动时，Spring Boot会：
   - 检测带有 `@Entity` 注解的类
   - 根据类名创建对应的数据库表（如 `todo` 表）
   - 根据字段类型创建对应的列
   - 执行 `CREATE TABLE IF NOT EXISTS` 语句

3. 每个字段会映射为数据库的一列：
   - `id` → `id` 列 (主键，自增)
   - `text` → `text` 列
   - `done` → `done` 列

4. 要查看生成的表，可访问：http://localhost:8080/h2-console

这样就无需手动创建数据库表，Spring Boot 会自动处理！

### 3. 数据访问 (相当于前端的状态管理)
```java
public interface TodoRepo extends JpaRepository<Todo, Long> {
    // 自动拥有findAll(), save(), deleteById()等方法
    // 类似前端的store.find(), store.add(), store.remove()
}
```

#### 注解详解

- 该接口没有显式注解，因为它继承了JpaRepository，Spring会自动为其生成实现
## API接口说明（前端重点关注）

| 方法 | 路径 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | `/api/todo` | 获取所有todos | 无 | `[{id: 1, text: "xxx", done: false}]` |
| POST | `/api/todo` | 创建todo | `{text: "新任务", done: false}` | `{id: 1, text: "新任务", done: false}` |
| DELETE | `/api/todo/{id}` | 删除todo | 无 | 无响应体 |

## 前后端交互示例

### 前端如何调用后端API
```javascript
// 获取所有todos
axios.get('/api/todo')  // 相当于 fetch('/api/todo')

// 创建新todo
axios.post('/api/todo', {text: "新任务", done: false})

// 删除todo
axios.delete('/api/todo/1')
```

## 项目运行步骤

1. **安装JDK 17**（相当于前端的Node.js）
2. **进入项目目录**
3. **运行命令**
   ```bash
   mvn spring-boot:run
   ```
4. **访问应用**
   - 应用：http://localhost:8080
   - 数据库控制台：http://localhost:8080/h2-console

## 与前端开发的相似性

| 前端开发 | 后端开发 | 说明 |
|----------|----------|------|
| React/Vue组件 | Controller类 | 处理业务逻辑 |
| useState/useReducer | Entity + Repository | 状态管理 |
| fetch/axios | @GetMapping/@PostMapping | API调用/定义 |
| package.json | pom.xml | 依赖管理 |
| public/index.html | src/main/resources/static/ | 静态资源存放 |

## 前端开发者快速上手技巧

1. **关注API设计**：重点理解Controller中的路径和参数
2. **理解数据流向**：@RequestBody是接收前端发送的数据，方法返回值是响应给前端的数据
3. **调试技巧**：后端有日志输出，可在控制台看到请求信息
4. **热重载**：修改后端代码后需重启应用（前端的热重载功能需要额外配置）

## 项目结构说明

```
crud-demo/                    # 项目根目录
├── pom.xml                   # 依赖管理文件（类似package.json）
├── src/main/java/            # Java源代码目录
│   └── com/example/demo/     # 包名目录
│       ├── DemoApplication.java  # 应用启动类
│       ├── Todo.java             # 数据模型
│       ├── TodoRepo.java         # 数据访问层
│       └── TodoController.java   # 控制器（API接口）
└── src/main/resources/       # 资源文件目录
    ├── application.properties # 配置文件
    └── static/               # 静态资源（前端文件）
        └── index.html        # 前端页面
```

## 重要注解汇总

| 注解 | 类型 | 作用 | 前端对比 |
|------|------|------|----------|
| `@SpringBootApplication` | 类 | 应用启动入口 | `ReactDOM.render()` 或 `createApp()` |
| `@RestController` | 类 | REST控制器 | API路由处理器 |
| `@Controller` | 类 | Web控制器 | 传统Web页面控制器 |
| `@Service` | 类 | 业务逻辑层 | 工具函数或服务模块 |
| `@Repository` | 类 | 数据访问层 | 数据库连接或ORM封装 |
| `@Component` | 类 | 通用组件 | 通用工具类 |
| `@Autowired` | 字段/方法 | 依赖注入 | props注入或context |
| `@RequestMapping` | 方法 | 通用请求映射 | 路由装饰器 |
| `@GetMapping` | 方法 | GET请求映射 | GET路由 |
| `@PostMapping` | 方法 | POST请求映射 | POST路由 |
| `@PutMapping` | 方法 | PUT请求映射 | PUT路由 |
| `@DeleteMapping` | 方法 | DELETE请求映射 | DELETE路由 |
| `@RequestParam` | 参数 | 查询参数 | URL查询参数处理 |
| `@PathVariable` | 参数 | 路径参数 | 动态路由参数 |
| `@RequestBody` | 参数 | 请求体参数 | 请求体解析 |
| `@ResponseBody` | 方法 | 响应体 | JSON响应 |
| `@Entity` | 类 | JPA实体 | 数据模型定义 |
| `@Table` | 类 | 数据库表映射 | 表结构定义 |
| `@Id` | 字段 | 主键标识 | 主键字段 |
| `@GeneratedValue` | 字段 | 主键自动生成 | 自增ID |
| `@Column` | 字段 | 列映射 | 字段映射 |
| `@OneToMany/@ManyToOne` | 字段 | 关系映射 | 关联关系 |

## 常见问题解答

### Q: 如何修改API路径？
A: 修改Controller类上的@RequestMapping注解，如改为`@RequestMapping("/api/v1/todo")`

### Q: 如何添加新的API接口？
A: 在TodoController中添加新方法，并使用相应注解：
```java
@PutMapping("/{id}")  // 更新操作
public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
    todo.setId(id);
    return repo.save(todo);
}
```

### Q: 如何查看数据库内容？
A: 访问 http://localhost:8080/h2-console，使用默认设置登录即可查看数据库内容

### Q: 如何调试后端代码？
A: 在IDEA中设置断点，以Debug模式运行应用，请求到达断点时会暂停执行

## 学习建议

1. 先熟悉API接口，了解前后端如何交互
2. 理解数据模型设计，这决定了前端如何处理数据
3. 学会阅读后端日志，这有助于调试问题
4. 逐步尝试修改代码，观察结果变化

通过这种方式，前端开发者可以更好地理解后端工作原理，提高全栈开发能力。