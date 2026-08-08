package com.example.shopapi.order.services;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.enums.OrderCancellationReason;
import com.example.shopapi.order.repositories.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderExpirationService {


    private final CustomerOrderRepository repository;

    private final OrderCancellationService cancellationService;


    public void cancelExpiredOrders(){

        List<CustomerOrder> orders =
                repository
                        .findByStatusAndPaymentExpiresAtBefore(
                                CustomerOrderStatus.PENDING,
                                LocalDateTime.now()
                        );


        for(CustomerOrder order : orders){

            cancellationService.cancel(order, OrderCancellationReason.PAYMENT_TIMEOUT);

        }
    }
}