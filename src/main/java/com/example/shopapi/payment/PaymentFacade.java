package com.example.shopapi.payment;

import com.example.shopapi.order.services.OrderWorkflowService;
import com.example.shopapi.auth.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderWorkflowService orderWorkflowService;
    private final AuthorizationService authorizationService;

    public void pay(
            Long orderId
    ) {
        Payment payment = paymentService.getEntity(orderId);

        authorizationService.requireOrderAccess(
                payment.getOrder()
        );

        orderWorkflowService.pay(
                payment.getOrder()
        );
    }

    public void refund(
            Long orderId
    ) {
        Payment payment = paymentService.getEntity(orderId);

        authorizationService.requireAdmin();

        paymentService.refund(payment);
    }

}