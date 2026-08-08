package com.example.shopapi.user.mappers;

import com.example.shopapi.user.dto.UpdateUserProfileRequest;
import com.example.shopapi.user.dto.UserProfileResponse;
import com.example.shopapi.user.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget User user, UpdateUserProfileRequest request);
}