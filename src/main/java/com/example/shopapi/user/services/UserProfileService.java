package com.example.shopapi.user.services;

import com.example.shopapi.common.exception.UserNotFoundException;
import com.example.shopapi.user.dto.UpdateUserProfileRequest;
import com.example.shopapi.user.dto.UserProfileResponse;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.mappers.UserProfileMapper;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper profileMapper;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(
            Long userId
    ) {
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(userId));

        return profileMapper.toResponse(user);
    }

    public UserProfileResponse updateMyProfile(
            Long userId,
            UpdateUserProfileRequest request
    ) {
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(userId));

        profileMapper.update(
                user,
                request
        );

        return profileMapper.toResponse(user);
    }
}