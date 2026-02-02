package com.example.demo.service;

import com.example.demo.exception.TodoNotFoundException;
import com.example.demo.model.Todo;
import com.example.demo.repository.TodoRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepo todoRepo;

    public TodoService(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    /**
     * 获取所有待办事项
     */
    public List<Todo> getAllTodos() {
        return todoRepo.findAll();
    }

    /**
     * 根据ID获取待办事项
     */
    public Optional<Todo> getTodoById(Long id) {
        return todoRepo.findById(id);
    }

    /**
     * 保存待办事项
     */
    public Todo saveTodo(Todo todo) {
        return todoRepo.save(todo);
    }

    /**
     * 删除待办事项
     */
    public void deleteTodo(Long id) {
        todoRepo.deleteById(id);
    }

    /**
     * 查找已完成的待办事项
     */
    public List<Todo> getCompletedTodos() {
        return todoRepo.findByDone();
    }

    /**
     * 根据文本内容模糊查找待办事项
     */
    public List<Todo> findTodosByText(String keyword) {
        return todoRepo.findByTextContaining(keyword);
    }

    /**
     * 使用原生SQL查找已完成的待办事项
     */
    public List<Todo> getCompletedTodosNative(boolean done) {
        return todoRepo.findByDoneNative(done);
    }

    /**
     * 使用原生SQL根据文本内容模糊查找待办事项
     */
    public List<Todo> findTodosByTextNative(String keyword) {
        return todoRepo.findByTextContainingNative(keyword);
    }

    /**
     * 更新待办事项的完成状态
     */
    @Transactional
    public int updateTodoStatus(Long id, boolean done) {
        return todoRepo.updateTodoStatus(id, done);
    }

    /**
     * 根据ID删除待办事项（使用自定义方法）
     */
    @Transactional
    public int deleteTodoByIdCustom(Long id) {
        return todoRepo.deleteByIdCustom(id);
    }

    /**
     * 获取待办事项总数
     */
    public Long getTodosCount() {
        return todoRepo.countTodos();
    }
    
    /**
     * 创建新的待办事项
     */
    public Todo createTodo(Todo todo) {
        todo.setDone(false); // 默认未完成
        return todoRepo.save(todo);
    }

    /**
     * 更新待办事项
     */
    public Todo updateTodo(Long id, Todo todoDetails) {
        Todo todo = todoRepo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        todo.setText(todoDetails.getText());
        todo.setDone(todoDetails.isDone());
        
        return todoRepo.save(todo);
    }

    /**
     * 切换待办事项完成状态
     */
    public Todo toggleTodoStatus(Long id) {
        Todo todo = todoRepo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        todo.setDone(!todo.isDone());
        
        return todoRepo.save(todo);
    }
}