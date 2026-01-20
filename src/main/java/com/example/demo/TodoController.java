package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;


    @GetMapping
    public List<Todo> all() { 
        return todoService.getAllTodos(); 
    }

    @PostMapping
    public Todo create(@RequestBody Todo todo) { 
        return todoService.createTodo(todo); 
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }
    
    @PatchMapping("/{id}/toggle")
    public Todo toggleStatus(@PathVariable Long id) {
        return todoService.toggleTodoStatus(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { 
        todoService.deleteTodo(id); 
    }
}