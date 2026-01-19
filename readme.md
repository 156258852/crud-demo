# 前端人 3 小时速成 BFF（Spring Boot）最小笔记

> 目标：从零到把一个 Todo CRUD 跑起来，让前端页面能调通。  
> 不需要系统学 Java，先抄后懂。

---

## 1 环境一次性装好
| 工具 | 下载地址 / 命令 | 备注 |
|---|---|---|
| JDK 17 | https://adoptium.net | 下一步一直点 |
| IDEA Community | https://jetbrains.com | 装插件 Spring Boot Helper |
| Node & 前端老本行 | 已有 | 无变化 |

---

## 2 新建项目（2 分钟）
1. 打开 https://start.spring.io  
   - Project: Maven  
   - Language: Java  
   - Spring Boot: 3.x  
   - Group: com.example  
   - Artifact: demo  
   - Dependencies: Spring Web, Spring Data JPA, H2 Database, Lombok  
2. 点击 GENERATE → 解压 → IDEA 打开 → 等待 Maven 下包完成。

---

## 3 最小必要代码（复制即用）
目录：src/main/java/com/example/demo

### 3.1 实体类 Todo.java
```java
package com.example.demo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Todo {
    @Id @GeneratedValue private Long id;
    private String text;
    private boolean done;
}
```

**注解说明：**
- `@Entity`：标识这是一个JPA实体类，对应数据库中的表
- `@Id`：标识主键字段
- `@GeneratedValue`：指定主键自动生成策略
- `@Data`：Lombok注解，自动生成getter/setter等方法

**数据库表自动生成说明：**

虽然我们只定义了实体类，但Spring Boot会根据 `application.properties` 中的配置自动创建数据库表：

- `spring.jpa.hibernate.ddl-auto=update` 配置使得Spring Boot在启动时自动创建或更新数据库表
- 每个 `@Entity` 注解的类会映射为一张数据库表
- 类中的字段会自动映射为表中的列
- 访问 http://localhost:8080/h2-console 可以查看生成的表结构

3.2 数据接口 TodoRepo.java
```java
package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<Todo, Long> { }
```

**注解说明：**
- 该接口继承了JpaRepository，Spring会自动为其生成实现，提供基本的CRUD操作

3.3 控制器 TodoController.java
```java 
package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoRepo repo;

    @GetMapping
    public List<Todo> all() { return repo.findAll(); }

    @PostMapping
    public Todo create(@RequestBody Todo todo) { return repo.save(todo); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
```

**注解说明：**
- `@RestController`：标识这是一个REST控制器，方法返回JSON数据
- `@RequestMapping("/api/todo")`：定义基础URL路径
- `@RequiredArgsConstructor`：Lombok注解，自动生成依赖注入构造函数
- `@GetMapping`：处理HTTP GET请求
- `@PostMapping`：处理HTTP POST请求
- `@DeleteMapping`：处理HTTP DELETE请求
- `@RequestBody`：将请求体转换为Java对象
- `@PathVariable`：从URL路径中提取参数

4 前端页面（放在 src/main/resources/static/index.html）
```html
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Todo BFF Demo</title>
  <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app">
  <h2>Todo List</h2>
  <input v-model="text" @keyup.enter="add">
  <button @click="add">新增</button>
  <ul>
    <li v-for="t in todos" :key="t.id">
      {{ t.text }}
      <button @click="del(t.id)">删</button>
    </li>
  </ul>
</div>
<script>
  const { createApp } = Vue;
  createApp({
    data() { return { todos:[], text:'' }; },
    mounted() { this.load(); },
    methods: {
      load()  { axios.get('/api/todo').then(r => this.todos = r.data); },
      add()   { axios.post('/api/todo',{text:this.text,done:false}).then(this.load); this.text=''; },
      del(id) { axios.delete('/api/todo/'+id).then(this.load); }
    }
  }).mount('#app');
</script>
</body>
</html>
```

5 运行起来（5 分钟）