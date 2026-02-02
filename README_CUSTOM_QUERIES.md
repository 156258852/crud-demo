# 自定义查询方法使用指南

本文档介绍了如何在项目中使用自定义的JPA查询方法。

## 目录
1. [简介](#简介)
2. [自定义查询方法列表](#自定义查询方法列表)
3. [查询方法详解](#查询方法详解)
4. [使用示例](#使用示例)
5. [最佳实践](#最佳实践)

## 简介

在本项目中，我们使用Spring Data JPA来处理数据库操作。除了JPA提供的标准CRUD方法外，我们还定义了一些自定义查询方法，以便更灵活地处理业务需求。

## 自定义查询方法列表

| 方法名 | 返回类型 | 参数 | 描述 |
|--------|----------|------|------|
| findByDone | List<Todo> | 无 | 查找已完成的待办事项 |
| findByTextContaining | List<Todo> | String keyword | 根据文本内容模糊查找待办事项 |
| findByDoneNative | List<Todo> | boolean done | 使用原生SQL查找已完成的待办事项 |
| findByTextContainingNative | List<Todo> | String keyword | 使用原生SQL根据文本内容模糊查找待办事项 |
| updateTodoStatus | int | Long id, boolean done | 更新待办事项的完成状态 |
| deleteByIdCustom | int | Long id | 根据ID删除待办事项（自定义方法） |
| countTodos | Long | 无 | 获取待办事项总数 |

## 查询方法详解

### 1. findByDone()
```java
@Query("SELECT t FROM Todo t WHERE t.done = true")
List<Todo> findByDone();
```
- **功能**: 查找所有已完成的待办事项
- **使用场景**: 获取已完成的任务列表
- **返回**: Todo对象列表

### 2. findByTextContaining(@Param("keyword") String keyword)
```java
@Query("SELECT t FROM Todo t WHERE t.text LIKE %:keyword%")
List<Todo> findByTextContaining(@Param("keyword") String keyword);
```
- **功能**: 根据关键词模糊搜索待办事项
- **使用场景**: 在搜索框中输入关键词查找相关任务
- **参数**: keyword - 搜索关键词
- **返回**: 包含关键词的Todo对象列表

### 3. findByDoneNative(boolean done)
```java
@Query(value = "SELECT * FROM todo WHERE done = ?1", nativeQuery = true)
List<Todo> findByDoneNative(boolean done);
```
- **功能**: 使用原生SQL查询特定完成状态的待办事项
- **使用场景**: 需要精确控制SQL查询时
- **参数**: done - 完成状态(true/false)
- **返回**: 符合条件的Todo对象列表

### 4. findByTextContainingNative(String keyword)
```java
@Query(value = "SELECT * FROM todo WHERE text LIKE %?1%", nativeQuery = true)
List<Todo> findByTextContainingNative(String keyword);
```
- **功能**: 使用原生SQL根据文本内容模糊查找待办事项
- **使用场景**: 需要精确控制SQL查询时
- **参数**: keyword - 搜索关键词
- **返回**: 包含关键词的Todo对象列表

### 5. updateTodoStatus(@Param("id") Long id, @Param("done") boolean done)
```java
@Modifying
@Query("UPDATE Todo t SET t.done = :done WHERE t.id = :id")
int updateTodoStatus(@Param("id") Long id, @Param("done") boolean done);
```
- **功能**: 更新待办事项的完成状态
- **使用场景**: 切换任务的完成状态
- **参数**: id - 待办事项ID, done - 新的完成状态
- **返回**: 受影响的记录数

### 6. deleteByIdCustom(@Param("id") Long id)
```java
@Modifying
@Query("DELETE FROM Todo t WHERE t.id = :id")
int deleteByIdCustom(@Param("id") Long id);
```
- **功能**: 根据ID删除待办事项
- **使用场景**: 删除特定的待办事项
- **参数**: id - 待办事项ID
- **返回**: 受影响的记录数

### 7. countTodos()
```java
@Query("SELECT COUNT(t) FROM Todo t")
Long countTodos();
```
- **功能**: 获取待办事项总数
- **使用场景**: 统计任务数量
- **返回**: 待办事项总数

## 使用示例

### 在Service中使用

```java
@Service
public class TodoService {
    private final TodoRepo todoRepo;

    public TodoService(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    // 获取已完成的待办事项
    public List<Todo> getCompletedTodos() {
        return todoRepo.findByDone();
    }

    // 根据文本搜索待办事项
    public List<Todo> findTodosByText(String keyword) {
        return todoRepo.findByTextContaining(keyword);
    }

    // 更新待办事项状态
    @Transactional
    public int updateTodoStatus(Long id, boolean done) {
        return todoRepo.updateTodoStatus(id, done);
    }
}
```

### 在Controller中使用

```java
@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    // 获取已完成的待办事项
    @GetMapping("/completed")
    public List<Todo> completedTodos() {
        return todoService.getCompletedTodos();
    }

    // 根据关键词搜索待办事项
    @GetMapping("/search")
    public List<Todo> searchTodos(@RequestParam String keyword) {
        return todoService.findTodosByText(keyword);
    }

    // 更新待办事项状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id, 
            @RequestParam boolean done) {
        int updatedRows = todoService.updateTodoStatus(id, done);
        if(updatedRows > 0) {
            return ResponseEntity.ok("Status updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Update failed");
        }
    }
}
```

## 最佳实践

1. **命名约定**: 使用有意义的方法名，使代码更具可读性
2. **参数验证**: 在使用自定义查询前验证参数的有效性
3. **异常处理**: 适当地处理可能发生的异常
4. **事务管理**: 对于修改数据的操作，使用@Transactional注解
5. **性能优化**: 对于大量数据的查询，考虑使用分页
6. **安全性**: 避免SQL注入，优先使用JPQL而不是原生SQL
7. **索引**: 为经常查询的字段创建数据库索引

## 注意事项

1. 使用@Modifying注解的方法需要在事务上下文中执行
2. 原生SQL查询与JPQL查询相比，移植性较差
3. 使用LIKE操作符时注意性能影响，特别是大数据集
4. 记得在需要的地方添加@Transactional注解以确保数据一致性