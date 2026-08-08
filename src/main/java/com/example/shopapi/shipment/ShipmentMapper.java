package com.example.shopapi.shipment;

import com.example.shopapi.shipment.dto.ShipmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(
            target = "orderId",
            source = "order.id"
    )
    ShipmentResponse toResponse(
            Shipment shipment
    );

}