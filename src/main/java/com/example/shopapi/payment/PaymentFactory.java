package com.example.shopapi.payment;

import com.example.shopapi.common.exception.businessExceptions.PaymentException;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.payment.enums.PaymentMethod;
import com.example.shopapi.payment.enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {

    public Payment create(
            CustomerOrder order,
            PaymentMethod method
    ) {
        if(method == null){
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