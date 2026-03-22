package com.example.demo.service;

import com.example.demo.exception.TodoNotFoundException;
import com.example.demo.model.Todo;
import com.example.demo.stub.TestTodoService;
import com.example.demo.stub.TodoRepoStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TodoService 单元测试
 * 
 * 使用 TestTodoService + TodoRepoStub 进行测试
 * 不依赖任何 Mock 框架，纯 JUnit 5 测试
 */
class TodoServiceTest {

    private TodoRepoStub todoRepoStub;
    private TestTodoService todoService;

    @BeforeEach
    void setUp() {
        todoRepoStub = new TodoRepoStub();
        todoService = new TestTodoService(todoRepoStub);
    }

    // ==================== getAllTodos ====================

    @Test
    @DisplayName("获取所有待办事项 - 成功返回列表")
    void getAllTodos_Success() {
        // Given - 准备测试数据
        todoRepoStub.addPresetData(1L, "任务1", false);
        todoRepoStub.addPresetData(2L, "任务2", true);

        // When - 执行测试方法
        List<Todo> result = todoService.getAllTodos();

        // Then - 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取所有待办事项 - 空列表")
    void getAllTodos_EmptyList() {
        // Given - 空数据

        // When
        List<Todo> result = todoService.getAllTodos();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getTodoById ====================

    @Test
    @DisplayName("根据ID获取待办事项 - 存在时返回")
    void getTodoById_Found() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", false);

        // When
        Optional<Todo> result = todoService.getTodoById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("测试任务", result.get().getText());
    }

    @Test
    @DisplayName("根据ID获取待办事项 - 不存在时返回空")
    void getTodoById_NotFound() {
        // Given - 没有数据

        // When
        Optional<Todo> result = todoService.getTodoById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    // ==================== createTodo ====================

    @Test
    @DisplayName("创建待办事项 - 成功创建")
    void createTodo_Success() {
        // Given
        Todo inputTodo = new Todo();
        inputTodo.setText("新任务");

        // When
        Todo result = todoService.createTodo(inputTodo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("新任务", result.getText());
        assertFalse(result.isDone()); // 验证默认值为 false
        assertEquals(1, todoRepoStub.getSaveCallCount());
    }

    @Test
    @DisplayName("创建待办事项 - 验证设置默认done为false")
    void createTodo_DefaultDoneFalse() {
        // Given
        Todo inputTodo = new Todo();
        inputTodo.setText("新任务");
        inputTodo.setDone(true); // 即使设置为 true

        // When
        Todo result = todoService.createTodo(inputTodo);

        // Then
        assertFalse(result.isDone()); // 应该被重置为 false
    }

    // ==================== saveTodo ====================

    @Test
    @DisplayName("保存待办事项 - 成功保存")
    void saveTodo_Success() {
        // Given
        Todo todo = new Todo();
        todo.setText("保存任务");

        // When
        Todo result = todoService.saveTodo(todo);

        // Then
        assertNotNull(result.getId());
        assertEquals(1, todoRepoStub.getSaveCallCount());
    }

    // ==================== updateTodo ====================

    @Test
    @DisplayName("更新待办事项 - 成功更新")
    void updateTodo_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "原始内容", false);
        
        Todo updateData = new Todo();
        updateData.setText("更新后的内容");
        updateData.setDone(true);

        // When
        Todo result = todoService.updateTodo(1L, updateData);

        // Then
        assertEquals("更新后的内容", result.getText());
        assertTrue(result.isDone());
        assertEquals(1, todoRepoStub.getSaveCallCount());
    }

    @Test
    @DisplayName("更新待办事项 - 不存在时抛出异常")
    void updateTodo_NotFound_ThrowsException() {
        // Given - 没有预设数据
        Todo updateData = new Todo();
        updateData.setText("更新内容");

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.updateTodo(999L, updateData);
        });
        
        // 验证没有调用 save
        assertEquals(0, todoRepoStub.getSaveCallCount());
    }

    // ==================== deleteTodo ====================

    @Test
    @DisplayName("删除待办事项 - 成功删除")
    void deleteTodo_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "要删除的任务", false);

        // When
        todoService.deleteTodo(1L);

        // Then
        assertEquals(1, todoRepoStub.getDeleteByIdCallCount());
        assertEquals(1L, todoRepoStub.getLastDeletedId());
        assertFalse(todoRepoStub.existsById(1L));
    }

    // ==================== toggleTodoStatus ====================

    @Test
    @DisplayName("切换状态 - 从未完成到完成")
    void toggleTodoStatus_FromFalseToTrue() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", false);

        // When
        Todo result = todoService.toggleTodoStatus(1L);

        // Then
        assertTrue(result.isDone());
    }

    @Test
    @DisplayName("切换状态 - 从完成到未完成")
    void toggleTodoStatus_FromTrueToFalse() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", true);

        // When
        Todo result = todoService.toggleTodoStatus(1L);

        // Then
        assertFalse(result.isDone());
    }

    @Test
    @DisplayName("切换状态 - 待办事项不存在时抛出异常")
    void toggleTodoStatus_NotFound_ThrowsException() {
        // Given - 没有预设数据

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.toggleTodoStatus(999L);
        });
    }

    // ==================== Custom Query Methods ====================

    @Test
    @DisplayName("获取已完成的待办事项")
    void getCompletedTodos_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "未完成任务", false);
        todoRepoStub.addPresetData(2L, "已完成任务1", true);
        todoRepoStub.addPresetData(3L, "已完成任务2", true);

        // When
        List<Todo> result = todoService.getCompletedTodos();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Todo::isDone));
    }

    @Test
    @DisplayName("获取已完成待办事项 - 无已完成项")
    void getCompletedTodos_Empty() {
        // Given
        todoRepoStub.addPresetData(1L, "未完成任务1", false);
        todoRepoStub.addPresetData(2L, "未完成任务2", false);

        // When
        List<Todo> result = todoService.getCompletedTodos();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据文本搜索待办事项")
    void findTodosByText_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "买牛奶", false);
        todoRepoStub.addPresetData(2L, "买面包", false);
        todoRepoStub.addPresetData(3L, "写代码", false);

        // When
        List<Todo> result = todoService.findTodosByText("买");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getText().contains("买")));
    }

    @Test
    @DisplayName("根据文本搜索待办事项 - 无匹配")
    void findTodosByText_NoMatch() {
        // Given
        todoRepoStub.addPresetData(1L, "买牛奶", false);
        todoRepoStub.addPresetData(2L, "买面包", false);

        // When
        List<Todo> result = todoService.findTodosByText("学习");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("使用原生SQL查找已完成的待办事项")
    void getCompletedTodosNative_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "未完成任务", false);
        todoRepoStub.addPresetData(2L, "已完成任务", true);

        // When
        List<Todo> result = todoService.getCompletedTodosNative(true);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isDone());
    }

    @Test
    @DisplayName("使用原生SQL查找未完成的待办事项")
    void getCompletedTodosNative_FindIncomplete() {
        // Given
        todoRepoStub.addPresetData(1L, "未完成任务", false);
        todoRepoStub.addPresetData(2L, "已完成任务", true);

        // When
        List<Todo> result = todoService.getCompletedTodosNative(false);

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isDone());
    }

    @Test
    @DisplayName("使用原生SQL根据文本搜索待办事项")
    void findTodosByTextNative_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "学习Java", false);
        todoRepoStub.addPresetData(2L, "学习Python", false);
        todoRepoStub.addPresetData(3L, "去跑步", false);

        // When
        List<Todo> result = todoService.findTodosByTextNative("学习");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("更新待办事项状态 - 成功")
    void updateTodoStatus_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "测试任务", false);

        // When
        int affectedRows = todoService.updateTodoStatus(1L, true);

        // Then
        assertEquals(1, affectedRows);
        assertTrue(todoRepoStub.findById(1L).get().isDone());
    }

    @Test
    @DisplayName("更新待办事项状态 - 待办事项不存在")
    void updateTodoStatus_NotFound() {
        // Given - 没有预设数据

        // When
        int affectedRows = todoService.updateTodoStatus(999L, true);

        // Then
        assertEquals(0, affectedRows);
    }

    @Test
    @DisplayName("根据ID删除待办事项（自定义方法）")
    void deleteTodoByIdCustom_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "要删除的任务", false);

        // When
        int result = todoService.deleteTodoByIdCustom(1L);

        // Then
        assertEquals(1, result);
        assertFalse(todoRepoStub.existsById(1L));
    }

    @Test
    @DisplayName("根据ID删除待办事项（自定义方法）- 不存在")
    void deleteTodoByIdCustom_NotFound() {
        // Given - 没有预设数据

        // When
        int result = todoService.deleteTodoByIdCustom(999L);

        // Then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("获取待办事项总数")
    void getTodosCount_Success() {
        // Given
        todoRepoStub.addPresetData(1L, "任务1", false);
        todoRepoStub.addPresetData(2L, "任务2", false);
        todoRepoStub.addPresetData(3L, "任务3", true);

        // When
        Long count = todoService.getTodosCount();

        // Then
        assertEquals(3L, count);
    }

    @Test
    @DisplayName("获取待办事项总数 - 空数据")
    void getTodosCount_Empty() {
        // Given - 空数据

        // When
        Long count = todoService.getTodosCount();

        // Then
        assertEquals(0L, count);
    }
}