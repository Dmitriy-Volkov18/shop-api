package com.example.shopapi.order.services;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.repositories.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;

    @Transactional
    public CustomerOrder save(
            CustomerOrder order
    ) {
        return customerOrderRepository.save(
                order
        );
    }


}