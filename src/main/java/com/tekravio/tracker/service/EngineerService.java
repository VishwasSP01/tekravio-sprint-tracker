package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.EngineerDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.EngineerRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EngineerService {

    private final EngineerRepository repository;

    public EngineerService(EngineerRepository repository) {
        this.repository = repository;
    }

    public EngineerDto.Response create(EngineerDto.Request request) {
        return DtoMapper.toResponse(repository.save(new Engineer(request.name(), request.email(),
                request.primaryStack(), request.experienceYears(), request.available())));
    }

    @Transactional(readOnly = true)
    public PageResponse<EngineerDto.Response> list(Pageable pageable) {
        return PageResponse.from(repository.findAll(pageable), DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EngineerDto.Response get(Long id) {
        return DtoMapper.toResponse(find(id));
    }

    public EngineerDto.Response update(Long id, EngineerDto.Request request) {
        Engineer engineer = find(id);
        engineer.setName(request.name());
        engineer.setEmail(request.email());
        engineer.setPrimaryStack(request.primaryStack());
        engineer.setExperienceYears(request.experienceYears());
        engineer.setAvailable(request.available());
        return DtoMapper.toResponse(engineer);
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public List<EngineerDto.Response> available(PrimaryStack stack) {
        return repository.findAvailableWithCapacity(stack).stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnalyticsDto.EngineerWorkload workload(Long id) {
        List<Task> tasks = find(id).getAssignedTasks();
        long active = tasks.stream().filter(task -> task.getStatus() != TaskStatus.DONE).count();
        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            byStatus.put(status, tasks.stream().filter(task -> task.getStatus() == status).count());
        }
        BigDecimal estimated = tasks.stream().map(Task::getEstimatedHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = tasks.stream().map(Task::getActualHours).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AnalyticsDto.EngineerWorkload(active, byStatus, estimated, actual);
    }

    Engineer find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Engineer", id));
    }
}
