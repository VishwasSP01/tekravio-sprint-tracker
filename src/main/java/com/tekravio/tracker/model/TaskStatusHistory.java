package com.tekravio.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_status_history")
public class TaskStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus newStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(nullable = false)
    private String changedBy;

    protected TaskStatusHistory() {
    }

    public TaskStatusHistory(Task task, TaskStatus previousStatus, TaskStatus newStatus, String changedBy) {
        this.task = task;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Task getTask() { return task; }
    public TaskStatus getPreviousStatus() { return previousStatus; }
    public TaskStatus getNewStatus() { return newStatus; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public String getChangedBy() { return changedBy; }
}
