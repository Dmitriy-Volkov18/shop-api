package com.example.shopapi.promotion.services;


import com.example.shopapi.order.repositories.CustomerOrderRepository;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPromotionService {

    private final CustomerOrderRepository orderRepository;

    public boolean isFirstOrder(
            User user
    ) {
        return !orderRepository.existsByUserId(
                user.getId()
        );
    }
}