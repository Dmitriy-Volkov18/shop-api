package com.example.shopapi.returnProducts;

import com.example.shopapi.returnProducts.dto.ReturnResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReturnMapper {
    @Mapping(
            target = "orderId",
            source = "order.id"
    )
    ReturnResponse toResponse(
            ReturnRequest request
    );

}