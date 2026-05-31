package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.TaskDto;
import com.tekravio.tracker.exception.EngineerNotAvailableException;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.exception.InvalidStatusTransitionException;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.SprintStatus;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.TaskRepository;
import com.tekravio.tracker.repository.TaskStatusHistoryRepository;
import com.tekravio.tracker.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SprintService sprintService;

    @Mock
    private EngineerService engineerService;

    @Mock
    private TaskStatusHistoryRepository historyRepository;

    @Mock
    private CurrentUserService currentUserService;

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
        when(currentUserService.username()).thenReturn("admin");

        TaskDto.Response response = taskService.updateStatus(1L, TaskStatus.DONE);

        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
        assertThat(response.completedAt()).isNotNull();
    }

    @Test
    void supportsCreateReadUpdateListAndDelete() {
        Sprint sprint = sprint();
        Engineer engineer = new Engineer("Available", "available@example.com", PrimaryStack.JAVA, 3, true);
        Task task = task(TaskStatus.TODO);
        TaskDto.Request request = request(TaskStatus.TODO, 1L, 2L);
        PageRequest pageable = PageRequest.of(0, 10);
        when(sprintService.find(1L)).thenReturn(sprint);
        when(engineerService.find(2L)).thenReturn(engineer);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Task>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(task)));
        when(taskRepository.findAllBySprintId(1L, pageable)).thenReturn(new PageImpl<>(List.of(task)));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThat(taskService.create(request).status()).isEqualTo(TaskStatus.TODO);
        assertThat(taskService.list(null, null, null, pageable).totalElements()).isEqualTo(1);
        assertThat(taskService.listBySprint(1L, pageable).totalElements()).isEqualTo(1);
        assertThat(taskService.get(1L).title()).isEqualTo("Task");
        assertThat(taskService.update(1L, request).assignedEngineerId()).isNull();

        taskService.delete(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void create_whenInitialStatusIsNotTodo_rejectsRequest() {
        assertThatThrownBy(() -> taskService.create(request(TaskStatus.DONE, 1L, null)))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void get_whenMissing_throwsNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.get(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private Task task(TaskStatus status) {
        return new Task("Task", "Description", TaskPriority.HIGH, status, new BigDecimal("4"),
                null, sprint(), null);
    }

    private Sprint sprint() {
        return new Sprint(1, "Goal", SprintStatus.IN_PROGRESS, LocalDate.now(),
                LocalDate.now().plusDays(7), null);
    }

    private TaskDto.Request request(TaskStatus status, Long sprintId, Long engineerId) {
        return new TaskDto.Request("Task", "Description", TaskPriority.HIGH, status,
                new BigDecimal("4"), null, sprintId, engineerId);
    }
}
