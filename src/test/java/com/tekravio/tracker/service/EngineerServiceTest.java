package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.EngineerDto;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.repository.EngineerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EngineerServiceTest {

    @Mock
    private EngineerRepository repository;

    @InjectMocks
    private EngineerService service;

    @Test
    void supportsCrudAvailabilityAndWorkload() {
        Engineer engineer = engineer();
        EngineerDto.Request request = new EngineerDto.Request("Aarav Updated", "aarav@example.com",
                PrimaryStack.JAVA, 5, true);
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.save(any(Engineer.class))).thenReturn(engineer);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(engineer)));
        when(repository.findAvailableWithCapacity(PrimaryStack.JAVA)).thenReturn(List.of(engineer));
        when(repository.findById(1L)).thenReturn(Optional.of(engineer));

        assertThat(service.create(request).name()).isEqualTo("Aarav");
        assertThat(service.list(pageable).totalElements()).isEqualTo(1);
        assertThat(service.get(1L).name()).isEqualTo("Aarav");
        assertThat(service.update(1L, request).experienceYears()).isEqualTo(5);
        assertThat(service.available(PrimaryStack.JAVA)).hasSize(1);
        assertThat(service.workload(1L).activeTasks()).isZero();

        service.delete(1L);
        verify(repository).delete(engineer);
    }

    @Test
    void get_whenMissing_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private Engineer engineer() {
        return new Engineer("Aarav", "aarav@example.com", PrimaryStack.JAVA, 4, true);
    }
}
