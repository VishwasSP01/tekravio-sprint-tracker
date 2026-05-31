package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.ClientDto;
import com.tekravio.tracker.dto.EngineerDto;
import com.tekravio.tracker.dto.ProjectDto;
import com.tekravio.tracker.dto.SprintDto;
import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.model.Client;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.Project;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.Task;

final class DtoMapper {

    private DtoMapper() {
    }

    static ClientDto.Response toResponse(Client client) {
        return new ClientDto.Response(client.getId(), client.getName(), client.getIndustry(),
                client.getContactEmail(), client.getCountry(), client.getCreatedAt());
    }

    static ProjectDto.Response toResponse(Project project) {
        return new ProjectDto.Response(project.getId(), project.getName(), project.getDescription(),
                project.getStatus(), project.getStartDate(), project.getEndDate(), project.getClient().getId());
    }

    static SprintDto.Response toResponse(Sprint sprint) {
        return new SprintDto.Response(sprint.getId(), sprint.getSprintNumber(), sprint.getGoal(),
                sprint.getStatus(), sprint.getStartDate(), sprint.getEndDate(), sprint.getProject().getId());
    }

    static EngineerDto.Response toResponse(Engineer engineer) {
        return new EngineerDto.Response(engineer.getId(), engineer.getName(), engineer.getEmail(),
                engineer.getPrimaryStack(), engineer.getExperienceYears(), engineer.isAvailable());
    }

    static TaskDto.Response toResponse(Task task) {
        return new TaskDto.Response(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getEstimatedHours(), task.getActualHours(), task.getSprint().getId(),
                task.getAssignedEngineer() == null ? null : task.getAssignedEngineer().getId(),
                task.getCreatedAt(), task.getCompletedAt());
    }
}
