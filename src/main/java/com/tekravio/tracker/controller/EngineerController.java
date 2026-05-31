package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.EngineerDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.service.EngineerService;
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

import java.util.List;

@RestController
@RequestMapping("/api/engineers")
public class EngineerController {

    private final EngineerService service;

    public EngineerController(EngineerService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<EngineerDto.Response>> create(@Valid @RequestBody EngineerDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Engineer created"));
    }

    @GetMapping
    ApiResponse<PageResponse<EngineerDto.Response>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(service.list(pageable), "Engineers retrieved");
    }

    @GetMapping("/{id}")
    ApiResponse<EngineerDto.Response> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id), "Engineer retrieved");
    }

    @PutMapping("/{id}")
    ApiResponse<EngineerDto.Response> update(@PathVariable Long id,
                                             @Valid @RequestBody EngineerDto.Request request) {
        return ApiResponse.success(service.update(id, request), "Engineer updated");
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null, "Engineer deleted");
    }

    @GetMapping("/available")
    ApiResponse<List<EngineerDto.Response>> available(@RequestParam(required = false) PrimaryStack stack) {
        return ApiResponse.success(service.available(stack), "Available engineers retrieved");
    }

    @GetMapping("/{id}/workload")
    ApiResponse<AnalyticsDto.EngineerWorkload> workload(@PathVariable Long id) {
        return ApiResponse.success(service.workload(id), "Engineer workload calculated");
    }
}
