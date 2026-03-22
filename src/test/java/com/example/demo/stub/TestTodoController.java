package com.example.demo.stub;

import com.example.demo.model.Todo;

import java.util.List;

/**
 * 用于测试的 TodoController
 * 
 * 直接使用 TestTodoService，避免依赖 Spring 框架
 * 包含与真正 TodoController 相同的业务逻辑
 */
public class TestTodoController {

    private final TestTodoService todoService;

    public TestTodoController(TestTodoService todoService) {
        this.todoService = todoService;
    }

    public List<Todo> all() {
        return todoService.getAllTodos();
    }

    public Todo create(Todo todo) {
        return todoService.createTodo(todo);
    }

    public Todo update(Long id, Todo todo) {
        return todoService.updateTodo(id, todo);
    }

    public void delete(Long id) {
        todoService.deleteTodo(id);
    }

    public Todo toggleStatus(Long id) {
        return todoService.toggleTodoStatus(id);
    }

    public TestTodoService getTodoService() {
        return todoService;
    }
}