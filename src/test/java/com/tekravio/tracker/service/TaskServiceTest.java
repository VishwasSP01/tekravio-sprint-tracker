package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.exception.EngineerNotAvailableException;
import com.tekravio.tracker.exception.InvalidStatusTransitionException;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.SprintStatus;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SprintService sprintService;

    @Mock
    private EngineerService engineerService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void assignEngineer_whenEngineerUnavailable_throwsException() {
        Task task = task(TaskStatus.TODO);
        Engineer engineer = new Engineer("Unavailable", "unavailable@example.com", PrimaryStack.QA, 3, false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(engineerService.find(2L)).thenReturn(engineer);

        assertThatThrownBy(() -> taskService.assignEngineer(1L, 2L))
                .isInstanceOf(EngineerNotAvailableException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void updateStatus_whenMovingBackwards_throwsException() {
        Task task = task(TaskStatus.REVIEW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateStatus(1L, TaskStatus.IN_PROGRESS))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("exactly one step");
    }

    @Test
    void updateStatus_whenCompletingTask_recordsCompletionTime() {
        Task task = task(TaskStatus.REVIEW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto.Response response = taskService.updateStatus(1L, TaskStatus.DONE);

        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
        assertThat(response.completedAt()).isNotNull();
    }

    private Task task(TaskStatus status) {
        Sprint sprint = new Sprint(1, "Goal", SprintStatus.IN_PROGRESS, LocalDate.now(),
                LocalDate.now().plusDays(7), null);
        return new Task("Task", "Description", TaskPriority.HIGH, status, new BigDecimal("4"),
                null, sprint, null);
    }
}
