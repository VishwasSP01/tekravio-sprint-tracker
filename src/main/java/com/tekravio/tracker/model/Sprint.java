package com.tekravio.tracker.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sprints", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sprint_project_number", columnNames = {"project_id", "sprint_number"})
})
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sprint_number", nullable = false)
    private int sprintNumber;

    @Column(nullable = false, length = 1000)
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    protected Sprint() {
    }

    public Sprint(int sprintNumber, String goal, SprintStatus status, LocalDate startDate,
                  LocalDate endDate, Project project) {
        this.sprintNumber = sprintNumber;
        this.goal = goal;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
    }

    public Long getId() { return id; }
    public int getSprintNumber() { return sprintNumber; }
    public void setSprintNumber(int sprintNumber) { this.sprintNumber = sprintNumber; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public SprintStatus getStatus() { return status; }
    public void setStatus(SprintStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public List<Task> getTasks() { return tasks; }
}
