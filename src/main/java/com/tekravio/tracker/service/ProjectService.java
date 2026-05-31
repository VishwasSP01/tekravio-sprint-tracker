package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.AnalyticsDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.dto.ProjectDto;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Project;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.ProjectRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repository;
    private final ClientService clientService;

    public ProjectService(ProjectRepository repository, ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    public ProjectDto.Response create(ProjectDto.Request request) {
        validateDates(request.startDate(), request.endDate());
        return DtoMapper.toResponse(repository.save(new Project(request.name(), request.description(),
                request.status(), request.startDate(), request.endDate(), clientService.findActive(request.clientId()))));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectDto.Response> list(Long clientId, Pageable pageable) {
        return PageResponse.from(clientId == null ? repository.findAll(pageable)
                : repository.findAllByClientId(clientId, pageable), DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectDto.Response get(Long id) {
        return DtoMapper.toResponse(find(id));
    }

    public ProjectDto.Response update(Long id, ProjectDto.Request request) {
        validateDates(request.startDate(), request.endDate());
        Project project = find(id);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setClient(clientService.findActive(request.clientId()));
        return DtoMapper.toResponse(project);
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public AnalyticsDto.ProjectHealth health(Long id) {
        List<Task> tasks = find(id).getSprints().stream().flatMap(sprint -> sprint.getTasks().stream()).toList();
        long completed = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        long overdue = tasks.stream().filter(ProjectService::isOverdue).count();
        double completion = percentage(completed, tasks.size());
        int score = (int) Math.round(Math.max(0, completion * 0.8 - Math.min(20, overdue * 5)));
        return new AnalyticsDto.ProjectHealth(score, completion, overdue,
                "score = max(0, completionPercentage * 0.8 - min(20, overdueTasks * 5))");
    }

    Project find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    private static boolean isOverdue(Task task) {
        return task.getStatus() != TaskStatus.DONE && task.getSprint().getEndDate().isBefore(LocalDate.now());
    }

    private static double percentage(long count, long total) {
        return total == 0 ? 0 : Math.round(count * 10000.0 / total) / 100.0;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new InvalidRequestException("Project end date cannot be before start date");
        }
    }
}
