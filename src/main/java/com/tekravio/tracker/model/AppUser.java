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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id", unique = true)
    private Engineer engineer;

    @Column(nullable = false)
    private boolean enabled;

    protected AppUser() {
    }

    public AppUser(String username, String passwordHash, UserRole role, Engineer engineer) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.engineer = engineer;
        this.enabled = true;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public Engineer getEngineer() { return engineer; }
    public boolean isEnabled() { return enabled; }
}
