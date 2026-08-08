package com.example.shopapi.user.services;

import com.example.shopapi.common.exception.ConflictException;
import com.example.shopapi.user.dto.UserCreateRequest;
import com.example.shopapi.user.dto.UserUpdateRequest;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateCreate(
            UserCreateRequest request
    ) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException(
                    "Email already exists"
            );
        }
    }

    public void validateUpdate(
            User user,
            UserUpdateRequest request
    ) {

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsernameAndIdNot(
                request.username(),
                user.getId()
        )) {

            throw new ConflictException(
                    "Username already exists"
            );
        }

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmailAndIdNot(
                request.email(),
                user.getId()
        )) {

            throw new ConflictException(
                    "Email already exists"
            );
        }
    }
}