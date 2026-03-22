package com.example.demo.service.impl;

import com.example.demo.dto.CreateTodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.UpdateTodoRequest;
import com.example.demo.exception.TodoNotFoundException;
import com.example.demo.model.Todo;
import com.example.demo.repository.TodoRepository;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public Page<TodoResponse> getAllTodos(Pageable pageable) {
        return todoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public Optional<TodoResponse> getTodoById(Long id) {
        return todoRepository.findById(id)
                .map(this::toResponse);
    }

    @Override
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    public List<TodoResponse> getCompletedTodos() {
        return todoRepository.findByDone().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findTodosByText(String keyword) {
        return todoRepository.findByTextContaining(keyword).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public int updateTodoStatus(Long id, boolean done) {
        return todoRepository.updateTodoStatus(id, done);
    }

    @Override
    public TodoResponse createTodo(CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.setText(request.getText());
        todo.setDone(request.getDone() != null ? request.getDone() : false);
        return toResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse updateTodo(Long id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));

        todo.setText(request.getText());
        if (request.getDone() != null) {
            todo.setDone(request.getDone());
        }

        return toResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse toggleTodoStatus(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));

        todo.setDone(!todo.isDone());

        return toResponse(todoRepository.save(todo));
    }

    private TodoResponse toResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .text(todo.getText())
                .done(todo.isDone())
                .build();
    }
}
