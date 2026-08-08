package com.example.shopapi.payment;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.common.exception.PaymentNotFoundException;
import com.example.shopapi.inventory.InventoryReservationService;
import com.example.shopapi.payment.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryReservationService reservationService;

    @Transactional(readOnly = true)
    public Payment getEntity(
            Long orderId
    ) {
        return paymentRepository
                .findByOrderIdWithOrder(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(orderId));
    }

    public Payment save(
            Payment payment
    ) {
        return paymentRepository.save(payment);
    }

    public void success(
            Payment payment
    ) {
        payment.markSuccess();
    }

    public void refund(
            Payment payment
    ) {
        payment.refund();
    }

    public void refundIfPaid(
            CustomerOrder order
    ){
        Payment payment = order.getPayment();

        if(payment == null){
            return;
        }

        if(!payment.getStatus().canRefund()){
            return;
        }

        refund(payment);
    }

    public void cancelIfPending(
            Payment payment
    ){

        if(payment == null){
            return;
        }

        if(payment.getStatus() != PaymentStatus.PENDING){
            return;
        }

        cancel(payment);
    }

    public void fail(
            Payment payment,
            String reason
    ) {
        payment.markFailed(reason);

        reservationService.releaseByOrder(
                payment.getOrder()
        );
    }

    public void cancel(
            Payment payment
    ) {
        payment.cancel();
    }
}