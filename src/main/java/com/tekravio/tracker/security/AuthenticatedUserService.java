package com.tekravio.tracker.security;

import com.tekravio.tracker.exception.ForbiddenOperationException;
import com.tekravio.tracker.exception.InvalidRequestException;
import com.tekravio.tracker.model.AppUser;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.UserRole;
import com.tekravio.tracker.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService implements CurrentUserService {

    private final AppUserRepository repository;

    public AuthenticatedUserService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public String username() {
        return current().getUsername();
    }

    @Override
    public void requireTaskUpdatePermission(Task task) {
        AppUser user = current();
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getEngineer() == null || task.getAssignedEngineer() == null
                || !user.getEngineer().getId().equals(task.getAssignedEngineer().getId())) {
            throw new ForbiddenOperationException("Engineers can only update tasks assigned to themselves");
        }
    }

    private AppUser current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidRequestException("Authenticated user is required");
        }
        return repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new InvalidRequestException("Authenticated user is not registered"));
    }
}
