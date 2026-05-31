package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.SprintDto;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Project;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.SprintStatus;
import com.tekravio.tracker.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SprintServiceTest {

    @Mock
    private SprintRepository repository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SprintService service;

    @Test
    void supportsCrudListsAndSummary() {
        Project project = org.mockito.Mockito.mock(Project.class);
        Sprint sprint = sprint(project);
        SprintDto.Request request = request(1L);
        PageRequest pageable = PageRequest.of(0, 10);
        when(projectService.find(1L)).thenReturn(project);
        when(repository.save(any(Sprint.class))).thenReturn(sprint);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sprint)));
        when(repository.findAllByProjectId(1L, pageable)).thenReturn(new PageImpl<>(List.of(sprint)));
        when(repository.findById(1L)).thenReturn(Optional.of(sprint));

        assertThat(service.create(request).sprintNumber()).isEqualTo(1);
        assertThat(service.list(null, pageable).totalElements()).isEqualTo(1);
        assertThat(service.list(1L, pageable).totalElements()).isEqualTo(1);
        assertThat(service.get(1L).goal()).isEqualTo("Goal");
        assertThat(service.update(1L, request).status()).isEqualTo(SprintStatus.IN_PROGRESS);
        assertThat(service.summary(1L).totalTasks()).isZero();

        service.delete(1L);
        verify(repository).delete(sprint);
    }

    @Test
    void rejectsInvalidDatesAndMissingSprints() {
        SprintDto.Request invalid = new SprintDto.Request(1, "Goal", SprintStatus.PLANNED,
                LocalDate.now(), LocalDate.now().minusDays(1), 1L);

        assertThatThrownBy(() -> service.create(invalid)).isInstanceOf(InvalidRequestException.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private Sprint sprint(Project project) {
        return new Sprint(1, "Goal", SprintStatus.IN_PROGRESS, LocalDate.now(),
                LocalDate.now().plusDays(7), project);
    }

    private SprintDto.Request request(Long projectId) {
        return new SprintDto.Request(1, "Goal", SprintStatus.IN_PROGRESS, LocalDate.now(),
                LocalDate.now().plusDays(7), projectId);
    }
}
