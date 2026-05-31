package com.tekravio.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "engineers")
public class Engineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimaryStack primaryStack;

    @Column(nullable = false)
    private int experienceYears;

    @Column(nullable = false)
    private boolean available;

    @OneToMany(mappedBy = "assignedEngineer")
    private List<Task> assignedTasks = new ArrayList<>();

    protected Engineer() {
    }

    public Engineer(String name, String email, PrimaryStack primaryStack, int experienceYears,
                    boolean available) {
        this.name = name;
        this.email = email;
        this.primaryStack = primaryStack;
        this.experienceYears = experienceYears;
        this.available = available;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public PrimaryStack getPrimaryStack() { return primaryStack; }
    public void setPrimaryStack(PrimaryStack primaryStack) { this.primaryStack = primaryStack; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public List<Task> getAssignedTasks() { return assignedTasks; }
}
