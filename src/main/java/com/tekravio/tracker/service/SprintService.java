package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.SprintDto;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.SprintRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SprintService {

    private final SprintRepository repository;
    private final ProjectService projectService;

    public SprintService(SprintRepository repository, ProjectService projectService) {
        this.repository = repository;
        this.projectService = projectService;
    }

    public SprintDto.Response create(SprintDto.Request request) {
        validateDates(request.startDate(), request.endDate());
        return DtoMapper.toResponse(repository.save(new Sprint(request.sprintNumber(), request.goal(),
                request.status(), request.startDate(), request.endDate(), projectService.find(request.projectId()))));
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintDto.Response> list(Long projectId, Pageable pageable) {
        return PageResponse.from(projectId == null ? repository.findAll(pageable)
                : repository.findAllByProjectId(projectId, pageable), DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SprintDto.Response get(Long id) {
        return DtoMapper.toResponse(find(id));
    }

    public SprintDto.Response update(Long id, SprintDto.Request request) {
        validateDates(request.startDate(), request.endDate());
        Sprint sprint = find(id);
        sprint.setSprintNumber(request.sprintNumber());
        sprint.setGoal(request.goal());
        sprint.setStatus(request.status());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());
        sprint.setProject(projectService.find(request.projectId()));
        return DtoMapper.toResponse(sprint);
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public AnalyticsDto.SprintSummary summary(Long id) {
        List<Task> tasks = find(id).getTasks();
        long completed = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        double averageHours = tasks.stream()
                .filter(task -> task.getCompletedAt() != null)
                .mapToLong(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()).toMinutes())
                .average()
                .orElse(0) / 60.0;
        Map<TaskPriority, Long> priorities = new EnumMap<>(TaskPriority.class);
        for (TaskPriority priority : TaskPriority.values()) {
            priorities.put(priority, tasks.stream().filter(task -> task.getPriority() == priority).count());
        }
        long overdue = tasks.stream().filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getSprint().getEndDate().isBefore(LocalDate.now())).count();
        return new AnalyticsDto.SprintSummary(tasks.size(), percentage(completed, tasks.size()),
                round(averageHours), priorities, overdue);
    }

    Sprint find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sprint", id));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("Sprint end date cannot be before start date");
        }
    }

    private static double percentage(long count, long total) {
        return total == 0 ? 0 : round(count * 100.0 / total);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
