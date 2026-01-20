package com.example.demo;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    
    private final TodoRepo todoRepo;

    public List<Todo> getAllTodos() {
        return todoRepo.findAll();
    }
    
    public Todo createTodo(Todo todo) {
        return todoRepo.save(todo);
    }
    
    public Todo updateTodo(Long id, Todo todoDetails) {
        Todo todo = todoRepo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        todo.setText(todoDetails.getText());
        todo.setDone(todoDetails.isDone());
        
        return todoRepo.save(todo);
    }
    
    public Todo toggleTodoStatus(Long id) {
        Todo todo = todoRepo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        todo.setDone(!todo.isDone());
        
        return todoRepo.save(todo);
    }
    
    public void deleteTodo(Long id) {
        Todo todo = todoRepo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        todoRepo.delete(todo);
    }
    
    public Optional<Todo> getTodoById(Long id) {
        return todoRepo.findById(id);
    }
}