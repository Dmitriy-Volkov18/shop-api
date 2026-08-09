package com.example.shopapi.recentlyViewed;

import com.example.shopapi.product.recentlyviewed.RedisRecentlyViewedService;
import com.example.shopapi.product.services.ProductEventService;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.product.enums.ProductEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentlyViewedService {
    private final RecentlyViewedRepository repository;
    private final ProductService productService;
    private final ProductEventService productEventService;
    private final RedisRecentlyViewedService redisRecentlyViewedService;

    public void addView(
            User user,
            Long productId
    ) {
        Product product = productService.getProduct(productId);

        RecentlyViewedProduct viewed =
                repository
                        .findByUserIdAndProductId(
                                user.getId(),
                                productId
                        )
                        .orElseGet(
                                RecentlyViewedProduct::new
                        );

        viewed.setUser(user);
        viewed.setProduct(product);
        viewed.setViewedAt(LocalDateTime.now());

        repository.save(viewed);

        redisRecentlyViewedService.add(
                user.getId(),
                productId
        );

        product.increaseViews();

        productEventService.record(
                product,
                ProductEventType.VIEW
        );
    }

    @Transactional(readOnly = true)
    public List<Product> getRedisViewed(
            User user
    ) {
        List<Long> ids =
                redisRecentlyViewedService.get(
                        user.getId()
                );


        return productService.findAllByIds(ids);
    }

    @Transactional(readOnly = true)
    public Page<RecentlyViewedProduct> getViewed(
            User user,
            Pageable pageable
    ) {
        return repository.findByUserIdOrderByViewedAtDesc(
                user.getId(),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public List<RecentlyViewedProduct> getItems(
            User user
    ) {
        return repository.findByUserIdOrderByViewedAtDesc(user.getId());
    }
}