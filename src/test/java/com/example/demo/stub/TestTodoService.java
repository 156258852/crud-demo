package com.example.demo.stub;

import com.example.demo.exception.TodoNotFoundException;
import com.example.demo.model.Todo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用于测试的 TodoService
 * 
 * 直接使用 TodoRepoStub，避免依赖 JpaRepository 接口
 * 包含与真正 TodoService 相同的业务逻辑
 */
public class TestTodoService {

    private final TodoRepoStub todoRepo;

    public TestTodoService(TodoRepoStub todoRepo) {
        this.todoRepo = todoRepo;
    }

    public List<Todo> getAllTodos() {
        return todoRepo.findAll();
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepo.findById(id);
    }

    public Todo saveTodo(Todo todo) {
        return todoRepo.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepo.deleteById(id);
    }

    public List<Todo> getCompletedTodos() {
        return todoRepo.findByDone();
    }

    public List<Todo> findTodosByText(String keyword) {
        return todoRepo.findByTextContaining(keyword);
    }

    public List<Todo> getCompletedTodosNative(boolean done) {
        return todoRepo.findByDoneNative(done);
    }

    public List<Todo> findTodosByTextNative(String keyword) {
        return todoRepo.findByTextContainingNative(keyword);
    }

    @Transactional
    public int updateTodoStatus(Long id, boolean done) {
        return todoRepo.updateTodoStatus(id, done);
    }

    @Transactional
    public int deleteTodoByIdCustom(Long id) {
        return todoRepo.deleteByIdCustom(id);
    }

    public Long getTodosCount() {
        return todoRepo.countTodos();
    }

    public Todo createTodo(Todo todo) {
        todo.setDone(false);
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

    public TodoRepoStub getTodoRepo() {
        return todoRepo;
    }
}