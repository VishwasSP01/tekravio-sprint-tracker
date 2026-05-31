package com.tekravio.tracker.security;

import com.tekravio.tracker.model.Task;

public interface CurrentUserService {
    String username();
    void requireTaskUpdatePermission(Task task);
}
