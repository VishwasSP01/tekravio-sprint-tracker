package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.exception.EngineerNotAvailableException;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.InvalidStatusTransitionException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.TaskRepository;
import com.tekravio.tracker.model.TaskStatusHistory;
import com.tekravio.tracker.repository.TaskStatusHistoryRepository;
import com.tekravio.tracker.security.CurrentUserService;
import com.tekravio.tracker.dto.TaskHistoryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;
    private final SprintService sprintService;
    private final EngineerService engineerService;
    private final TaskStatusHistoryRepository historyRepository;
    private final CurrentUserService currentUserService;

    public TaskService(TaskRepository repository, SprintService sprintService, EngineerService engineerService,
                       TaskStatusHistoryRepository historyRepository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.sprintService = sprintService;
        this.engineerService = engineerService;
        this.historyRepository = historyRepository;
        this.currentUserService = currentUserService;
    }

    public TaskDto.Response create(TaskDto.Request request) {
        if (request.status() != TaskStatus.TODO) {
            throw new InvalidRequestException("New tasks must start with TODO status");
        }
        Sprint sprint = sprintService.find(request.sprintId());
        Engineer engineer = request.assignedEngineerId() == null ? null : assignableEngineer(request.assignedEngineerId());
        Task task = new Task(request.title(), request.description(), request.priority(), request.status(),
                request.estimatedHours(), request.actualHours(), sprint, engineer);
        return DtoMapper.toResponse(repository.save(task));
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskDto.Response> list(TaskStatus status, TaskPriority priority, Long sprintId,
                                                Pageable pageable) {
        return PageResponse.from(repository.findAll(TaskSpecifications.matches(status, priority, sprintId), pageable),
                DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskDto.Response> listBySprint(Long sprintId, Pageable pageable) {
        sprintService.find(sprintId);
        return PageResponse.from(repository.findAllBySprintId(sprintId, pageable), DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskDto.Response get(Long id) {
        return DtoMapper.toResponse(find(id));
    }

    public TaskDto.Response update(Long id, TaskDto.Request request) {
        Task task = find(id);
        currentUserService.requireTaskUpdatePermission(task);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setEstimatedHours(request.estimatedHours());
        task.setActualHours(request.actualHours());
        task.setSprint(sprintService.find(request.sprintId()));
        task.setAssignedEngineer(request.assignedEngineerId() == null ? null
                : assignableEngineer(request.assignedEngineerId()));
        if (request.status() != task.getStatus()) {
            updateStatus(task, request.status());
        }
        return DtoMapper.toResponse(task);
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    public TaskDto.Response updateStatus(Long id, TaskStatus requestedStatus) {
        Task task = find(id);
        currentUserService.requireTaskUpdatePermission(task);
        updateStatus(task, requestedStatus);
        return DtoMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryDto> history(Long taskId) {
        find(taskId);
        return historyRepository.findAllByTaskIdOrderByChangedAtAsc(taskId).stream()
                .map(history -> new TaskHistoryDto(history.getId(), history.getTask().getId(),
                        history.getPreviousStatus(), history.getNewStatus(), history.getChangedAt(),
                        history.getChangedBy()))
                .toList();
    }

    public TaskDto.Response assignEngineer(Long taskId, Long engineerId) {
        Task task = find(taskId);
        task.setAssignedEngineer(assignableEngineer(engineerId));
        return DtoMapper.toResponse(task);
    }

    Task find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    private Engineer assignableEngineer(Long id) {
        Engineer engineer = engineerService.find(id);
        if (!engineer.isAvailable()) {
            throw new EngineerNotAvailableException(id);
        }
        return engineer;
    }

    private void updateStatus(Task task, TaskStatus requestedStatus) {
        if (requestedStatus.ordinal() != task.getStatus().ordinal() + 1) {
            throw new InvalidStatusTransitionException(
                    "Task status must advance exactly one step from " + task.getStatus() + " to " + requestedStatus);
        }
        TaskStatus previousStatus = task.getStatus();
        task.setStatus(requestedStatus);
        historyRepository.save(new TaskStatusHistory(task, previousStatus, requestedStatus,
                currentUserService.username()));
        if (requestedStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }
    }
}
