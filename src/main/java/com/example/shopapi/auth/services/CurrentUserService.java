package com.example.shopapi.auth.services;

import com.example.shopapi.user.CustomUserPrincipal;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.enums.UserRole;
import com.example.shopapi.common.exception.UserNotFoundException;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public CustomUserPrincipal getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserPrincipal currentUser)) {
            throw new AccessDeniedException("Invalid authenticated principal");
        }

        return currentUser;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public UserRole getCurrentRole() {
        return getCurrentUser().getRole();
    }

    public User getCurrentUserEntity() {
        Long id = getCurrentUserId();

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));
    }
}