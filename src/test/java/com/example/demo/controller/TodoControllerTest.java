package com.example.demo.controller;

import com.example.demo.exception.TodoNotFoundException;
import com.example.demo.model.Todo;
import com.example.demo.stub.TestTodoController;
import com.example.demo.stub.TestTodoService;
import com.example.demo.stub.TodoRepoStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TodoController 单元测试
 * 
 * 使用 TestTodoController + TestTodoService + TodoRepoStub 进行测试
 * 不依赖 Spring 框架，纯 JUnit 5 测试
 */
class TodoControllerTest {

    private TodoRepoStub todoRepoStub;
    private TestTodoService todoService;
    private TestTodoController todoController;

    @BeforeEach
    void setUp() {
        todoRepoStub = new TodoRepoStub();
        todoService = new TestTodoService(todoRepoStub);
        todoController = new TestTodoController(todoService);
    }

    // ==================== GET /api/todo (all) ====================

    @Test
    @DisplayName("获取所有待办事项 - 返回列表")
    void all_ReturnsTodoList() {
        // Given
        todoRepoStub.addPresetData(1L, "任务1", false);
        todoRepoStub.addPresetData(2L, "任务2", true);

        // When
        List<Todo> result = todoController.all();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取所有待办事项 - 空列表")
    void all_ReturnsEmptyList() {
        // Given - 空数据

        // When
        List<Todo> result = todoController.all();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== POST /api/todo (create) ====================

    @Test
    @DisplayName("创建待办事项 - 成功创建")
    void create_Success() {
        // Given
        Todo inputTodo = new Todo();
        inputTodo.setText("新任务");

        // When
        Todo result = todoController.create(inputTodo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("新任务", result.getText());
        assertFalse(result.isDone()); // 验证默认值为 false
        assertEquals(1, todoRepoStub.getSaveCallCount());
    }

    @Test
    @DisplayName("创建待办事项 - 验证调用计数")
    void create_VerifyCallCount() {
        // Given
        Todo todo1 = new Todo();
        todo1.setText("任务1");
        Todo todo2 = new Todo();
        todo2.setText("任务2");

        // When
        todoController.create(todo1);
        todoController.create(todo2);

        // Then
        assertEquals(2, todoRepoStub.getSaveCallCount());
    }

    // ==================== PUT /api/todo/{id} (update) ====================

    @Test
    @DisplayName("更新待办事项 - 成功更新")
    void update_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "原始内容", false);
        
        Todo updateData = new Todo();
        updateData.setText("更新内容");
        updateData.setDone(true);

        // When
        Todo result = todoController.update(1L, updateData);

        // Then
        assertEquals("更新内容", result.getText());
        assertTrue(result.isDone());
    }

    @Test
    @DisplayName("更新待办事项 - 不存在时抛出异常")
    void update_NotFound_ThrowsException() {
        // Given - 没有预设数据
        Todo updateData = new Todo();
        updateData.setText("更新内容");

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> {
            todoController.update(999L, updateData);
        });
    }

    // ==================== DELETE /api/todo/{id} (delete) ====================

    @Test
    @DisplayName("删除待办事项 - 成功删除")
    void delete_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "要删除的任务", false);

        // When
        todoController.delete(1L);

        // Then
        assertEquals(1, todoRepoStub.getDeleteByIdCallCount());
        assertFalse(todoRepoStub.existsById(1L));
    }

    @Test
    @DisplayName("删除待办事项 - 多次删除")
    void delete_MultipleTimes() {
        // Given
        todoRepoStub.addPresetData(1L, "任务1", false);
        todoRepoStub.addPresetData(2L, "任务2", false);

        // When
        todoController.delete(1L);
        todoController.delete(2L);

        // Then
        assertEquals(2, todoRepoStub.getDeleteByIdCallCount());
    }

    // ==================== PATCH /api/todo/{id}/toggle (toggleStatus) ====================

    @Test
    @DisplayName("切换状态 - 从未完成到完成")
    void toggleStatus_FromFalseToTrue() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", false);

        // When
        Todo result = todoController.toggleStatus(1L);

        // Then
        assertTrue(result.isDone());
    }

    @Test
    @DisplayName("切换状态 - 从完成到未完成")
    void toggleStatus_FromTrueToFalse() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", true);

        // When
        Todo result = todoController.toggleStatus(1L);

        // Then
        assertFalse(result.isDone());
    }

    @Test
    @DisplayName("切换状态 - 不存在时抛出异常")
    void toggleStatus_NotFound_ThrowsException() {
        // Given - 没有预设数据

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> {
            todoController.toggleStatus(999L);
        });
    }

    // ==================== 综合测试 ====================

    @Test
    @DisplayName("综合场景 - 创建、更新、切换、删除")
    void integratedScenario_CreateUpdateToggleDelete() {
        // Given - 初始为空

        // When - 创建
        Todo newTodo = new Todo();
        newTodo.setText("新任务");
        Todo created = todoController.create(newTodo);

        // Then - 验证创建
        assertNotNull(created.getId());

        // When - 更新
        Todo updateData = new Todo();
        updateData.setText("更新后的任务");
        updateData.setDone(false);
        Todo updated = todoController.update(created.getId(), updateData);

        // Then - 验证更新
        assertEquals("更新后的任务", updated.getText());

        // When - 切换状态
        Todo toggled = todoController.toggleStatus(created.getId());

        // Then - 验证切换
        assertTrue(toggled.isDone());

        // When - 删除
        todoController.delete(created.getId());

        // Then - 验证删除
        assertFalse(todoRepoStub.existsById(created.getId()));
    }

    @Test
    @DisplayName("综合场景 - 批量操作")
    void integratedScenario_BatchOperations() {
        // Given - 预设多条数据
        todoRepoStub.addPresetData(1L, "任务1", false);
        todoRepoStub.addPresetData(2L, "任务2", false);
        todoRepoStub.addPresetData(3L, "任务3", true);

        // When - 获取全部
        List<Todo> allTodos = todoController.all();

        // Then
        assertEquals(3, allTodos.size());

        // When - 批量切换状态
        todoController.toggleStatus(1L);
        todoController.toggleStatus(2L);
        todoController.toggleStatus(3L);

        // Then - 验证状态变化
        assertTrue(todoRepoStub.findById(1L).get().isDone());
        assertTrue(todoRepoStub.findById(2L).get().isDone());
        assertFalse(todoRepoStub.findById(3L).get().isDone());

        // When - 删除已完成的项目
        todoController.delete(1L);
        todoController.delete(2L);

        // Then - 验证剩余数量
        List<Todo> remaining = todoController.all();
        assertEquals(1, remaining.size());
    }
}