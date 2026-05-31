package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.ProjectDto;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Client;
import com.tekravio.tracker.model.Project;
import com.tekravio.tracker.model.ProjectStatus;
import com.tekravio.tracker.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ProjectService service;

    @Test
    void supportsCrudListsAndHealthCalculation() {
        Client client = new Client("Acme", "Tech", "ops@acme.example", "India");
        Project project = project(client);
        ProjectDto.Request request = request(1L);
        PageRequest pageable = PageRequest.of(0, 10);
        when(clientService.findActive(1L)).thenReturn(client);
        when(repository.save(any(Project.class))).thenReturn(project);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(project)));
        when(repository.findAllByClientId(1L, pageable)).thenReturn(new PageImpl<>(List.of(project)));
        when(repository.findById(1L)).thenReturn(Optional.of(project));

        assertThat(service.create(request).name()).isEqualTo("Platform");
        assertThat(service.list(null, pageable).totalElements()).isEqualTo(1);
        assertThat(service.list(1L, pageable).totalElements()).isEqualTo(1);
        assertThat(service.get(1L).name()).isEqualTo("Platform");
        assertThat(service.update(1L, request).status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(service.health(1L).score()).isZero();

        service.delete(1L);
        verify(repository).delete(project);
    }

    @Test
    void rejectsInvalidDatesAndMissingProjects() {
        ProjectDto.Request invalid = new ProjectDto.Request("Platform", "Description", ProjectStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().minusDays(1), 1L);

        assertThatThrownBy(() -> service.create(invalid)).isInstanceOf(InvalidRequestException.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private Project project(Client client) {
        return new Project("Platform", "Description", ProjectStatus.ACTIVE, LocalDate.now(), null, client);
    }

    private ProjectDto.Request request(Long clientId) {
        return new ProjectDto.Request("Platform", "Description", ProjectStatus.ACTIVE,
                LocalDate.now(), null, clientId);
    }
}
