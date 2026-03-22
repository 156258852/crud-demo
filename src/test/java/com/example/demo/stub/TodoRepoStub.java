package com.example.demo.stub;

import com.example.demo.model.Todo;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * TodoRepo 的简化 Stub 实现
 * 
 * 不实现 JpaRepository 接口，提供与 TodoRepo 相同的方法签名
 * 用于 Service 和 Controller 层单元测试
 */
public class TodoRepoStub {

    private final Map<Long, Todo> dataStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 用于跟踪方法调用
    private int saveCallCount = 0;
    private int deleteByIdCallCount = 0;
    private Long lastDeletedId = null;
    private Todo lastSavedTodo = null;

    // ==================== 模拟 Repository 方法 ====================

    public List<Todo> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    public Todo save(Todo todo) {
        saveCallCount++;
        lastSavedTodo = todo;
        if (todo.getId() == null) {
            todo.setId(idGenerator.getAndIncrement());
        }
        dataStore.put(todo.getId(), todo);
        return todo;
    }

    public void deleteById(Long id) {
        deleteByIdCallCount++;
        lastDeletedId = id;
        dataStore.remove(id);
    }

    public void delete(Todo entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    public void deleteAll() {
        dataStore.clear();
    }

    public long count() {
        return dataStore.size();
    }

    public boolean existsById(Long id) {
        return dataStore.containsKey(id);
    }

    // ==================== 自定义查询方法实现 ====================

    public List<Todo> findByDone() {
        return dataStore.values().stream()
                .filter(Todo::isDone)
                .collect(Collectors.toList());
    }

    public List<Todo> findByTextContaining(String keyword) {
        return dataStore.values().stream()
                .filter(todo -> todo.getText() != null && todo.getText().contains(keyword))
                .collect(Collectors.toList());
    }

    public List<Todo> findByDoneNative(boolean done) {
        return dataStore.values().stream()
                .filter(todo -> todo.isDone() == done)
                .collect(Collectors.toList());
    }

    public List<Todo> findByTextContainingNative(String keyword) {
        return findByTextContaining(keyword);
    }

    public int updateTodoStatus(Long id, boolean done) {
        Todo todo = dataStore.get(id);
        if (todo != null) {
            todo.setDone(done);
            return 1;
        }
        return 0;
    }

    public int deleteByIdCustom(Long id) {
        if (dataStore.containsKey(id)) {
            deleteById(id);
            return 1;
        }
        return 0;
    }

    public Long countTodos() {
        return (long) dataStore.size();
    }

    // ==================== Stub 辅助方法 ====================

    /**
     * 添加预置数据
     */
    public Todo addPresetData(Long id, String text, boolean done) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setText(text);
        todo.setDone(done);
        dataStore.put(id, todo);
        if (idGenerator.get() <= id) {
            idGenerator.set(id + 1);
        }
        return todo;
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        dataStore.clear();
        idGenerator.set(1);
        saveCallCount = 0;
        deleteByIdCallCount = 0;
        lastDeletedId = null;
        lastSavedTodo = null;
    }

    /**
     * 获取 save 方法调用次数
     */
    public int getSaveCallCount() {
        return saveCallCount;
    }

    /**
     * 获取 deleteById 方法调用次数
     */
    public int getDeleteByIdCallCount() {
        return deleteByIdCallCount;
    }

    /**
     * 获取最后一次删除的 ID
     */
    public Long getLastDeletedId() {
        return lastDeletedId;
    }

    /**
     * 获取最后一次保存的 Todo
     */
    public Todo getLastSavedTodo() {
        return lastSavedTodo;
    }

    /**
     * 获取数据存储（用于验证）
     */
    public Map<Long, Todo> getDataStore() {
        return dataStore;
    }
}