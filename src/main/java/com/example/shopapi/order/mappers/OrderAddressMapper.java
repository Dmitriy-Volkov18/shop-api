package com.example.shopapi.order.mappers;

import com.example.shopapi.order.dto.OrderAddressResponse;
import com.example.shopapi.order.entities.OrderAddressSnapshot;
import com.example.shopapi.user.entities.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderAddressMapper {

    OrderAddressResponse toResponse(OrderAddressSnapshot address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderAddressSnapshot toSnapshot(UserAddress address);
}