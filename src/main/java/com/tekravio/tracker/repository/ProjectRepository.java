package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByClientId(Long clientId, Pageable pageable);
}
