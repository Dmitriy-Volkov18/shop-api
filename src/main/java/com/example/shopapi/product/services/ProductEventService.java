package com.example.shopapi.product.services;

import com.example.shopapi.common.infrastructure.redis.RedisTrendingService;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.entities.ProductEvent;
import com.example.shopapi.product.enums.ProductEventType;
import com.example.shopapi.product.repositories.ProductEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductEventService {

    private final ProductEventRepository repository;
    private final RedisTrendingService redisTrendingService;
    private final TrendingScoreCalculator scoreCalculator;

    public void record(
            Product product,
            ProductEventType type
    ) {
        record(product, type, 1);
    }

    public void record(
            Product product,
            ProductEventType type,
            int quantity
    ) {
        ProductEvent event = new ProductEvent();
        event.setProduct(product);
        event.setType(type);
        event.setQuantity(quantity);
        event.setCreatedAt(LocalDateTime.now());

        repository.save(event);

        redisTrendingService.increaseScore(
                product.getId(),
                scoreCalculator.calculate(
                        type,
                        quantity
                )
        );

    }

}