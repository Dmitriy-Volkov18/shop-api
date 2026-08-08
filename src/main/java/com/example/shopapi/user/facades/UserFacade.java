package com.example.shopapi.user.facades;

import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.user.UserFilter;
import com.example.shopapi.user.dto.UpdateUserProfileRequest;
import com.example.shopapi.user.dto.UserCreateRequest;
import com.example.shopapi.user.dto.UserProfileResponse;
import com.example.shopapi.user.dto.UserResponse;
import com.example.shopapi.user.dto.UserUpdateRequest;
import com.example.shopapi.user.services.UserProfileService;
import com.example.shopapi.user.services.UserService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;
    private final UserProfileService userProfileService;

    public UserResponse create(UserCreateRequest request) {
        User user = userService.create(request);

        return userMapper.toResponse(user);
    }

    public UserResponse get(Long id) {
        User user = userService.getUser(id);
        authorizationService.requireUserAccess(user);

        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getUsers(
            UserFilter filter,
            Pageable pageable
    ) {
        return userService.getUsers(filter, pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse update(
            Long id,
            UserUpdateRequest request
    ) {
        User user = userService.getUser(id);
        authorizationService.requireUserAccess(user);
        User updated = userService.update(user, request);

        return userMapper.toResponse(updated);
    }

    public void delete(Long id) {
        User user = userService.getUser(id);
        authorizationService.requireUserAccess(user);
        userService.delete(user);
    }

    public UserProfileResponse getMyProfile() {
        Long userId = currentUserService.getCurrentUserId();

        return userProfileService.getMyProfile(userId);
    }

    public UserProfileResponse updateMyProfile(UpdateUserProfileRequest request) {
        Long userId = currentUserService.getCurrentUserId();

        return userProfileService.updateMyProfile(userId, request);
    }
}