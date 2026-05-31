package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineerRepository extends JpaRepository<Engineer, Long> {
}
