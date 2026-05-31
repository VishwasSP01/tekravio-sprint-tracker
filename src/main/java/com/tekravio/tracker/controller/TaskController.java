package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<TaskDto.Response>> create(@Valid @RequestBody TaskDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Task created"));
    }

    @GetMapping
    ApiResponse<PageResponse<TaskDto.Response>> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long sprintId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(service.list(status, priority, sprintId, pageable), "Tasks retrieved");
    }

    @GetMapping("/{id}")
    ApiResponse<TaskDto.Response> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id), "Task retrieved");
    }

    @PutMapping("/{id}")
    ApiResponse<TaskDto.Response> update(@PathVariable Long id,
                                         @Valid @RequestBody TaskDto.Request request) {
        return ApiResponse.success(service.update(id, request), "Task updated");
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null, "Task deleted");
    }

    @PutMapping("/{id}/status")
    ApiResponse<TaskDto.Response> updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody TaskDto.StatusRequest request) {
        return ApiResponse.success(service.updateStatus(id, request.status()), "Task status updated");
    }

    @PutMapping("/{id}/assign/{engineerId}")
    ApiResponse<TaskDto.Response> assign(@PathVariable Long id, @PathVariable Long engineerId) {
        return ApiResponse.success(service.assignEngineer(id, engineerId), "Engineer assigned");
    }
}
