package com.example.shopapi.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{orderId}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pay(
            @PathVariable Long orderId
    ) {
        paymentFacade.pay(orderId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refund(
            @PathVariable Long orderId
    ) {
        paymentFacade.refund(orderId);
    }

}