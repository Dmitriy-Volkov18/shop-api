package com.example.shopapi.order.services;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.common.exception.CustomerOrderNotFoundException;
import com.example.shopapi.order.CustomerOrderFilter;
import com.example.shopapi.order.repositories.CustomerOrderRepository;
import com.example.shopapi.order.CustomerOrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
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