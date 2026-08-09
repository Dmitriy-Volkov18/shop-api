package com.example.shopapi.user.controllers;

import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.user.CustomUserPrincipal;
import com.example.shopapi.user.dto.MeResponse;
import com.example.shopapi.user.dto.UpdateUserProfileRequest;
import com.example.shopapi.user.dto.UserCreateRequest;
import com.example.shopapi.user.dto.UserProfileResponse;
import com.example.shopapi.user.dto.UserResponse;
import com.example.shopapi.user.dto.UserUpdateRequest;
import com.example.shopapi.user.facades.UserFacade;
import com.example.shopapi.user.UserFilter;
import com.example.shopapi.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;
    private final CurrentUserService currentUserService;

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userFacade.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userFacade.get(id);
    }

    @GetMapping
    public Page<UserResponse> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        UserFilter filter = new UserFilter();
        filter.setUsername(username);
        filter.setEmail(email);
        filter.setRole(role);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        return userFacade.getUsers(filter, pageable);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userFacade.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userFacade.delete(id);
    }

    @GetMapping("/me")
    public MeResponse me() {
        CustomUserPrincipal user = currentUserService.getCurrentUser();

        return new MeResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole()
        );
    }

    @GetMapping("/me/profile")
    public UserProfileResponse myProfile() {
        return userFacade.getMyProfile();
    }

    @PutMapping("/me/profile")
    public UserProfileResponse updateProfile(@RequestBody UpdateUserProfileRequest request) {
        return userFacade.updateMyProfile(request);
    }
}