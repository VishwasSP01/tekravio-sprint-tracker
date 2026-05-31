package com.tekravio.tracker.controller;

import com.tekravio.tracker.dto.ApiResponse;
import com.tekravio.tracker.dto.ClientDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.service.ClientService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<ClientDto.Response>> create(@Valid @RequestBody ClientDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Client created"));
    }

    @GetMapping
    ApiResponse<PageResponse<ClientDto.Response>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(service.list(pageable), "Clients retrieved");
    }

    @GetMapping("/{id}")
    ApiResponse<ClientDto.Response> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id), "Client retrieved");
    }

    @PutMapping("/{id}")
    ApiResponse<ClientDto.Response> update(@PathVariable Long id,
                                            @Valid @RequestBody ClientDto.Request request) {
        return ApiResponse.success(service.update(id, request), "Client updated");
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null, "Client deleted");
    }
}
