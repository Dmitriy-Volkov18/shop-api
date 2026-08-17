package com.example.shopapi.payment;

import com.example.shopapi.order.services.OrderWorkflowService;
import com.example.shopapi.auth.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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

        orderWorkflowService.pay(payment.getOrder());

        log.info("Order is paid");
    }

    public void refund(
            Long orderId
    ) {
        Payment payment = paymentService.getEntity(orderId);
        authorizationService.requireAdmin();
        paymentService.refund(payment);

        log.info("Order is refunded");
    }

}