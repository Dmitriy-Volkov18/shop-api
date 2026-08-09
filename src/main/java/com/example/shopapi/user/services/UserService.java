package com.example.shopapi.user.services;

import com.example.shopapi.common.exception.notFoundExceptions.UserNotFoundException;
import com.example.shopapi.user.UserFilter;
import com.example.shopapi.user.dto.UserCreateRequest;
import com.example.shopapi.user.dto.UserUpdateRequest;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.mappers.UserMapper;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.shopapi.user.UserSpecification.hasEmail;
import static com.example.shopapi.user.UserSpecification.hasRole;
import static com.example.shopapi.user.UserSpecification.hasUsername;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<User> getUsers(
            UserFilter filter,
            Pageable pageable
    ) {
        Specification<User> spec =
                Specification.where(hasUsername(filter.getUsername()))
                        .and(hasEmail(filter.getEmail()))
                        .and(hasRole(filter.getRole()));

        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    public User update(
            User user,
            UserUpdateRequest request
    ) {
        userValidator.validateUpdate(
                user,
                request
        );

        userMapper.updateEntity(
                request,
                user
        );

        return user;
    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public User create(
            UserCreateRequest request
    ) {
        userValidator.validateCreate(request);

        User user = userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        return userRepository.save(user);
    }

}