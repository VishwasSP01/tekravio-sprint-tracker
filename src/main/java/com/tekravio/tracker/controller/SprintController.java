package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.SprintDto;
import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.service.SprintService;
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
@RequestMapping("/api/sprints")
public class SprintController {

    private final SprintService service;
    private final TaskService taskService;

    public SprintController(SprintService service, TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @PostMapping
    ResponseEntity<ApiResponse<SprintDto.Response>> create(@Valid @RequestBody SprintDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Sprint created"));
    }

    @GetMapping
    ApiResponse<PageResponse<SprintDto.Response>> list(@RequestParam(required = false) Long projectId,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(service.list(projectId, pageable), "Sprints retrieved");
    }

    @GetMapping("/{id}")
    ApiResponse<SprintDto.Response> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id), "Sprint retrieved");
    }

    @PutMapping("/{id}")
    ApiResponse<SprintDto.Response> update(@PathVariable Long id,
                                           @Valid @RequestBody SprintDto.Request request) {
        return ApiResponse.success(service.update(id, request), "Sprint updated");
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null, "Sprint deleted");
    }

    @PostMapping("/{id}/tasks")
    ResponseEntity<ApiResponse<TaskDto.Response>> createTask(@PathVariable Long id,
                                                             @Valid @RequestBody TaskDto.Request request) {
        if (!id.equals(request.sprintId())) {
            throw new com.tekravio.tracker.exception.InvalidRequestException(
                    "Path sprint id must match request sprint id");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(taskService.create(request), "Task created"));
    }

    @GetMapping("/{id}/tasks")
    ApiResponse<PageResponse<TaskDto.Response>> tasks(@PathVariable Long id,
                                                      @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(taskService.listBySprint(id, pageable), "Sprint tasks retrieved");
    }

    @GetMapping("/{id}/summary")
    ApiResponse<AnalyticsDto.SprintSummary> summary(@PathVariable Long id) {
        return ApiResponse.success(service.summary(id), "Sprint summary calculated");
    }
}
