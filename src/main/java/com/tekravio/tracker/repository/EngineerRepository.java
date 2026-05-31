package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EngineerRepository extends JpaRepository<Engineer, Long>, JpaSpecificationExecutor<Engineer> {
    @Query("""
            select engineer from Engineer engineer
            where engineer.available = true
              and (:stack is null or engineer.primaryStack = :stack)
              and (select count(task) from Task task
                   where task.assignedEngineer = engineer and task.status <> com.tekravio.tracker.model.TaskStatus.DONE) < 3
            order by engineer.name
            """)
    List<Engineer> findAvailableWithCapacity(PrimaryStack stack);
}
