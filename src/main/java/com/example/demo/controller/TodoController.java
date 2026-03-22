package com.example.demo.controller;

import com.example.demo.dto.CreateTodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.UpdateTodoRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Tag(name = "Todo管理", description = "待办事项的增删改查操作")
@SecurityRequirement(name = "Bearer")
public class TodoController {
    private final TodoService todoService;

    @Operation(summary = "获取所有待办事项（分页）", description = "支持分页和排序，返回待办事项分页列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待办事项列表")
    })
    @GetMapping 
    public ApiResponse<Page<TodoResponse>> all(
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ApiResponse.success(todoService.getAllTodos(pageable));
    }

    @Operation(summary = "获取所有待办事项（不分页）", description = "返回所有待办事项列表，不分页")
    @GetMapping("/all")
    public ApiResponse<List<TodoResponse>> allWithoutPagination() {
        return ApiResponse.success(todoService.getAllTodos());
    }

    @Operation(summary = "创建新的待办事项", description = "创建一个新的待办事项并返回其详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功创建待办事项",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TodoResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数验证失败")
    })
    @PostMapping
    public ApiResponse<TodoResponse> create(
            @Parameter(description = "待办事项详情") 
            @Valid @RequestBody CreateTodoRequest request) {
        return ApiResponse.success(todoService.createTodo(request));
    }

    @Operation(summary = "更新待办事项", description = "根据ID更新待办事项的详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新待办事项",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数验证失败"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @PutMapping("/{id}")
    public ApiResponse<TodoResponse> update(
            @Parameter(description = "待办事项ID") @PathVariable Long id,
            @Parameter(description = "待办事项更新详情") 
            @Valid @RequestBody UpdateTodoRequest request) {
        return ApiResponse.success(todoService.updateTodo(id, request));
    }

    @Operation(summary = "切换待办事项完成状态", description = "根据ID切换待办事项的完成状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功切换状态",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @PatchMapping("/{id}/toggle")
    public ApiResponse<TodoResponse> toggleStatus(
            @Parameter(description = "待办事项ID") @PathVariable Long id) {
        return ApiResponse.success(todoService.toggleTodoStatus(id));
    }

    @Operation(summary = "删除待办事项", description = "根据ID删除指定的待办事项")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功删除待办事项"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "待办事项不存在")
    })
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        todoService.deleteTodo(id);
        return ApiResponse.success();
    }
}