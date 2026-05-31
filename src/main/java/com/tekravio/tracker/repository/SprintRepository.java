package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
}
