package com.example.demo.service;

import com.example.demo.dto.CreateTodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.UpdateTodoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TodoService {

    Page<TodoResponse> getAllTodos(Pageable pageable);

    List<TodoResponse> getAllTodos();

    Optional<TodoResponse> getTodoById(Long id);

    void deleteTodo(Long id);

    List<TodoResponse> getCompletedTodos();

    List<TodoResponse> findTodosByText(String keyword);

    int updateTodoStatus(Long id, boolean done);

    TodoResponse createTodo(CreateTodoRequest request);

    TodoResponse updateTodo(Long id, UpdateTodoRequest request);

    TodoResponse toggleTodoStatus(Long id);
}
