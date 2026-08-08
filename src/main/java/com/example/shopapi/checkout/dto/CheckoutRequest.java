package com.example.shopapi.checkout.dto;

import com.example.shopapi.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        @Size(max = 50)
        String couponCode,

        @NotNull
        PaymentMethod paymentMethod
) {
        public PaymentMethod paymentMethodOrDefault() {
                return paymentMethod == null
                        ? PaymentMethod.CARD
                        : paymentMethod;
        }
}
