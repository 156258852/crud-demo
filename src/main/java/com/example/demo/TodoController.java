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
    public List<Todo> all() { 
        return repo.findAll(); }

    @PostMapping
    public Todo create(@RequestBody Todo todo) { return repo.save(todo); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}