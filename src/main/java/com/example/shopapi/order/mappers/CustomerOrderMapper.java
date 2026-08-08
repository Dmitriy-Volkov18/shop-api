package com.example.shopapi.order.mappers;

import com.example.shopapi.order.dto.CustomerOrderResponse;
import com.example.shopapi.order.entities.CustomerOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = {
                CustomerOrderItemMapper.class,
                OrderAddressMapper.class,
        }
)
public interface CustomerOrderMapper {

    @Mapping(
            target = "userId",
            source = "user.id"
    )
    @Mapping(
            target = "paymentStatus",
            source = "payment.status"
    )
    @Mapping(
            target = "shipmentStatus",
            source = "shipment.status"
    )
    @Mapping(
            target = "returnStatus",
            source = "returnRequest.status"
    )
    @Mapping(
            target="paidAt",
            source="payment.paidAt"
    )

    @Mapping(
            target="shippedAt",
            source="shipment.shippedAt"
    )

    @Mapping(
            target="deliveredAt",
            source="shipment.deliveredAt"
    )
    CustomerOrderResponse toResponse(
            CustomerOrder order
    );

    default Page<CustomerOrderResponse> toResponse(
            Page<CustomerOrder> page
    ) {
        return page.map(this::toResponse);
    }

    List<CustomerOrderResponse> toResponse(
            List<CustomerOrder> orders
    );
}