package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Tag(name = "Todo管理", description = "待办事项的增删改查操作")
public class TodoController {
    private final TodoService todoService;


    @Operation(summary = "获取所有待办事项", description = "返回所有的待办事项列表")
    @GetMapping
    public List<Todo> all() { 
        return todoService.getAllTodos(); 
    }

    @Operation(summary = "创建新的待办事项", description = "创建一个新的待办事项并返回其详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功创建待办事项",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Todo.class)))
    })
    @PostMapping
    public Todo create(@Parameter(description = "待办事项详情") @RequestBody Todo todo) { 
        return todoService.createTodo(todo); 
    }

    @Operation(summary = "更新待办事项", description = "根据ID更新待办事项的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新待办事项",
            content = @Content(schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @PutMapping("/{id}")
    public Todo update(@Parameter(description = "待办事项ID") @PathVariable Long id, 
                      @Parameter(description = "待办事项更新详情") @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }
    
    @Operation(summary = "切换待办事项完成状态", description = "根据ID切换待办事项的完成状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功切换状态",
            content = @Content(schema = @Schema(implementation = Todo.class))),
        @ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @PatchMapping("/{id}/toggle")
    public Todo toggleStatus(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        return todoService.toggleTodoStatus(id);
    }

    @Operation(summary = "删除待办事项", description = "根据ID删除指定的待办事项")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功删除待办事项"),
        @ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "待办事项ID") @PathVariable Long id) { 
        todoService.deleteTodo(id); 
    }
}