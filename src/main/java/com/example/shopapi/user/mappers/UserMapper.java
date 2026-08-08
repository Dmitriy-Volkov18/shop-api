package com.example.shopapi.user.mappers;

import com.example.shopapi.user.dto.UserCreateRequest;
import com.example.shopapi.user.dto.UserResponse;
import com.example.shopapi.user.dto.UserUpdateRequest;
import com.example.shopapi.user.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateRequest request);

    void updateEntity(UserUpdateRequest request,
                      @MappingTarget User user);
}