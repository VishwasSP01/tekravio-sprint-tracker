package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.TaskStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Long> {
    List<TaskStatusHistory> findAllByTaskIdOrderByChangedAtAsc(Long taskId);
}
