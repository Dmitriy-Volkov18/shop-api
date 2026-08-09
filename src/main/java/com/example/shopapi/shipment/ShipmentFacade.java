package com.example.shopapi.shipment;

import com.example.shopapi.shipment.dto.ShipRequest;
import com.example.shopapi.shipment.dto.ShipmentResponse;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.order.services.OrderWorkflowService;
import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentFacade {
    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;
    private final OrderWorkflowService orderWorkflowService;

    public ShipmentResponse get(Long id){
        if(authorizationService.isAdmin()){
            return shipmentMapper.toResponse(
                    shipmentService.getById(id)
            );
        }

        User user = currentUserService.getCurrentUserEntity();

        return shipmentMapper.toResponse(
                shipmentService.getForUser(
                        id,
                        user.getId()
                )
        );
    }

    public void process(
            Long id
    ){
        Shipment shipment = shipmentService.getById(id);
        shipmentService.process(shipment);
    }

    public void ship(
            Long id,
            ShipRequest request
    ){
        Shipment shipment = shipmentService.getById(id);

        orderWorkflowService.ship(
                shipment.getOrder(),
                request.carrier(),
                request.trackingNumber()
        );
    }

    public void deliver(
            Long id
    ){
        Shipment shipment = shipmentService.getById(id);

        orderWorkflowService.deliver(
                shipment.getOrder()
        );
    }

}