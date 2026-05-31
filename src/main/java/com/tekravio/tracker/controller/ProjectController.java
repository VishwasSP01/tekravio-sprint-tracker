package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.ProjectDto;
import com.tekravio.tracker.dto.SprintDto;
import com.tekravio.tracker.service.ProjectService;
import com.tekravio.tracker.service.SprintService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;
    private final SprintService sprintService;

    public ProjectController(ProjectService service, SprintService sprintService) {
        this.service = service;
        this.sprintService = sprintService;
    }

    @PostMapping
    ResponseEntity<ApiResponse<ProjectDto.Response>> create(@Valid @RequestBody ProjectDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Project created"));
    }

    @GetMapping
    ApiResponse<PageResponse<ProjectDto.Response>> list(@RequestParam(required = false) Long clientId,
                                                         @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(service.list(clientId, pageable), "Projects retrieved");
    }

    @GetMapping("/{id}")
    ApiResponse<ProjectDto.Response> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id), "Project retrieved");
    }

    @PutMapping("/{id}")
    ApiResponse<ProjectDto.Response> update(@PathVariable Long id,
                                             @Valid @RequestBody ProjectDto.Request request) {
        return ApiResponse.success(service.update(id, request), "Project updated");
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null, "Project deleted");
    }

    @GetMapping("/{id}/sprints")
    ApiResponse<PageResponse<SprintDto.Response>> sprints(@PathVariable Long id,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        service.get(id);
        return ApiResponse.success(sprintService.list(id, pageable), "Project sprints retrieved");
    }

    @GetMapping("/{id}/health")
    ApiResponse<AnalyticsDto.ProjectHealth> health(@PathVariable Long id) {
        return ApiResponse.success(service.health(id), "Project health calculated");
    }
}
