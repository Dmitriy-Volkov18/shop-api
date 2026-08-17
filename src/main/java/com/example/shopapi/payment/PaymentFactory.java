package com.example.shopapi.payment;

import com.example.shopapi.common.exception.businessExceptions.PaymentException;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.payment.enums.PaymentMethod;
import com.example.shopapi.payment.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentFactory {

    public Payment create(
            CustomerOrder order,
            PaymentMethod method
    ) {
        if(method == null){
            log.warn("Payment method is required");

            throw new PaymentException(
                    "Payment method is required"
            );
        }

        Payment payment = new Payment();
        payment.setAmount(order.getTotalPrice());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        return payment;
    }
}