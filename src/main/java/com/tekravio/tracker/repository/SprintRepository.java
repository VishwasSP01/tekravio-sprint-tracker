package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    Page<Sprint> findAllByProjectId(Long projectId, Pageable pageable);
}
