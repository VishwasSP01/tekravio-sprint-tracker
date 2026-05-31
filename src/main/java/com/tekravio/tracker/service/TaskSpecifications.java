package com.tekravio.tracker.service;

import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

final class TaskSpecifications {

    private TaskSpecifications() {
    }

    static Specification<Task> matches(TaskStatus status, TaskPriority priority, Long sprintId) {
        return Specification.allOf(
                equal("status", status),
                equal("priority", priority),
                equalNested("sprint", "id", sprintId));
    }

    private static <T> Specification<Task> equal(String field, T value) {
        return value == null ? null : (root, query, builder) -> builder.equal(root.get(field), value);
    }

    private static <T> Specification<Task> equalNested(String parent, String field, T value) {
        return value == null ? null : (root, query, builder) -> builder.equal(root.get(parent).get(field), value);
    }
}
