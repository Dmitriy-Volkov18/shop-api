package com.example.shopapi.order.mappers;

import com.example.shopapi.user.dto.AddressRequest;
import com.example.shopapi.user.dto.AddressResponse;
import com.example.shopapi.user.entities.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(UserAddress address);


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "user",
            ignore = true
    )
    @Mapping(
            target = "primaryAddress",
            ignore = true
    )
    UserAddress toEntity(AddressRequest request);


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "user",
            ignore = true
    )
    @Mapping(
            target = "primaryAddress",
            ignore = true
    )
    void update(
            AddressRequest request,
            @MappingTarget UserAddress address
    );
}