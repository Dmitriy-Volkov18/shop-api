package com.example.shopapi.order.mappers;

import com.example.shopapi.order.dto.CustomerOrderItemResponse;
import com.example.shopapi.order.entities.CustomerOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerOrderItemMapper {

    @Mapping(
            target = "variantId",
            source = "variant.id"
    )
    CustomerOrderItemResponse toResponse(
            CustomerOrderItem item
    );
}