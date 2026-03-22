package com.example.demo.repository;

import com.example.demo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TodoRepository 集成测试
 * 
 * 使用 @DataJpaTest 配置 H2 内存数据库进行测试
 * 这是 Repository 层的标准测试方式
 * 
 * 注意：Repository 层通常使用集成测试而非单元测试
 * 因为需要验证 JPA 映射和 SQL 查询的正确性
 */
@DataJpaTest
@ActiveProfiles("test")
class TodoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoRepository todoRepo;

    @BeforeEach
    void setUp() {
        // 清空数据库
        todoRepo.deleteAll();
    }

    // ==================== 基础 CRUD 操作 (JpaRepository 提供) ====================

    @Test
    @DisplayName("保存待办事项 - 成功")
    void save_Success() {
        // Given
        Todo todo = new Todo();
        todo.setText("测试任务");
        todo.setDone(false);

        // When
        Todo saved = todoRepo.save(todo);

        // Then
        assertNotNull(saved.getId());
        assertEquals("测试任务", saved.getText());
        assertFalse(saved.isDone());
    }

    @Test
    @DisplayName("根据ID查找 - 存在时返回")
    void findById_Found() {
        // Given
        Todo todo = new Todo();
        todo.setText("测试任务");
        todo.setDone(false);
        entityManager.persistAndFlush(todo);

        // When
        Optional<Todo> found = todoRepo.findById(todo.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("测试任务", found.get().getText());
    }

    @Test
    @DisplayName("根据ID查找 - 不存在时返回空")
    void findById_NotFound() {
        // When
        Optional<Todo> found = todoRepo.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("查找所有待办事项")
    void findAll_ReturnsAll() {
        // Given
        Todo todo1 = createTodo("任务1", false);
        Todo todo2 = createTodo("任务2", true);
        entityManager.persist(todo1);
        entityManager.persist(todo2);
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("更新待办事项")
    void save_Update() {
        // Given
        Todo todo = new Todo();
        todo.setText("原始内容");
        todo.setDone(false);
        entityManager.persistAndFlush(todo);

        // When
        todo.setText("更新内容");
        todo.setDone(true);
        Todo updated = todoRepo.save(todo);

        // Then
        assertEquals("更新内容", updated.getText());
        assertTrue(updated.isDone());
    }

    @Test
    @DisplayName("删除待办事项")
    void deleteById_Success() {
        // Given
        Todo todo = new Todo();
        todo.setText("要删除的任务");
        entityManager.persistAndFlush(todo);
        Long id = todo.getId();

        // When
        todoRepo.deleteById(id);

        // Then
        assertFalse(todoRepo.findById(id).isPresent());
    }

    @Test
    @DisplayName("统计待办事项数量")
    void count_ReturnsCorrectNumber() {
        // Given
        entityManager.persist(createTodo("任务1", false));
        entityManager.persist(createTodo("任务2", true));
        entityManager.flush();

        // When
        long count = todoRepo.count();

        // Then
        assertEquals(2, count);
    }

    // ==================== 自定义查询方法测试 ====================

    @Test
    @DisplayName("查找已完成的待办事项 (JPQL)")
    void findByDone_ReturnsCompleted() {
        // Given
        entityManager.persist(createTodo("未完成任务", false));
        entityManager.persist(createTodo("已完成任务1", true));
        entityManager.persist(createTodo("已完成任务2", true));
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findByDone();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Todo::isDone));
    }

    @Test
    @DisplayName("查找已完成的待办事项 - 无已完成项")
    void findByDone_NoCompleted() {
        // Given
        entityManager.persist(createTodo("任务1", false));
        entityManager.persist(createTodo("任务2", false));
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findByDone();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据文本模糊查找 (JPQL)")
    void findByTextContaining_Found() {
        // Given
        entityManager.persist(createTodo("买牛奶", false));
        entityManager.persist(createTodo("买面包", false));
        entityManager.persist(createTodo("写代码", false));
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findByTextContaining("买");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getText().contains("买")));
    }

    @Test
    @DisplayName("根据文本模糊查找 - 无匹配")
    void findByTextContaining_NoMatch() {
        // Given
        entityManager.persist(createTodo("买牛奶", false));
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findByTextContaining("学习");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("使用原生SQL查找指定状态的待办事项")
    void findByDoneNative_ReturnsCorrectStatus() {
        // Given
        entityManager.persist(createTodo("未完成", false));
        entityManager.persist(createTodo("已完成", true));
        entityManager.flush();

        // When
        List<Todo> completedList = todoRepo.findByDoneNative(true);
        List<Todo> incompleteList = todoRepo.findByDoneNative(false);

        // Then
        assertEquals(1, completedList.size());
        assertTrue(completedList.get(0).isDone());

        assertEquals(1, incompleteList.size());
        assertFalse(incompleteList.get(0).isDone());
    }

    @Test
    @DisplayName("使用原生SQL根据文本模糊查找")
    void findByTextContainingNative_Found() {
        // Given
        entityManager.persist(createTodo("学习Java", false));
        entityManager.persist(createTodo("学习Python", false));
        entityManager.persist(createTodo("去跑步", false));
        entityManager.flush();

        // When
        List<Todo> result = todoRepo.findByTextContainingNative("学习");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("更新待办事项状态")
    void updateTodoStatus_Success() {
        // Given
        Todo todo = createTodo("测试任务", false);
        entityManager.persistAndFlush(todo);
        Long id = todo.getId();

        // When
        int affectedRows = todoRepo.updateTodoStatus(id, true);

        // Then
        assertEquals(1, affectedRows);
        
        // 需要刷新才能看到更新
        entityManager.flush();
        entityManager.clear();
        
        Todo updated = entityManager.find(Todo.class, id);
        assertTrue(updated.isDone());
    }

    @Test
    @DisplayName("更新待办事项状态 - 不存在的ID")
    void updateTodoStatus_NotFound() {
        // When
        int affectedRows = todoRepo.updateTodoStatus(999L, true);

        // Then
        assertEquals(0, affectedRows);
    }

    // ==================== 辅助方法 ====================

    private Todo createTodo(String text, boolean done) {
        Todo todo = new Todo();
        todo.setText(text);
        todo.setDone(done);
        return todo;
    }
}