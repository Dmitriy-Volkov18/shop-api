package com.example.shopapi.returnProducts.services;

import com.example.shopapi.inventory.InventoryService;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.payment.PaymentService;
import com.example.shopapi.returnProducts.ReturnRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnWorkflowService {

    private final ReturnService returnService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;


    public void approve(
            ReturnRequest request
    ) {
        returnService.approve(request);
    }


    public void reject(
            ReturnRequest request
    ){

        returnService.reject(request);

        request.getOrder()
                .rejectReturn();
    }


    public void complete(
            ReturnRequest request
    ) {

        CustomerOrder order =
                request.getOrder();


        paymentService.refundIfPaid(
                order
        );


        inventoryService.restoreFromOrder(
                order
        );


        returnService.complete(
                request
        );


        order.completeReturn();
    }
}