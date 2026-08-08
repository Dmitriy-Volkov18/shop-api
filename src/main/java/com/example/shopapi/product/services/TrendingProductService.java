package com.example.shopapi.product.services;

import com.example.shopapi.common.config.TrendingProperties;
import com.example.shopapi.common.infrastructure.redis.RedisTrendingService;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.repositories.ProductEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingProductService {

    private final ProductEventRepository repository;
    private final TrendingProperties properties;
    private final RedisTrendingService redisTrendingService;
    private final ProductService productService;

/*    public Page<Product> getTrending(
            Pageable pageable
    ) {
        return repository.findTrending(
                LocalDateTime.now()
                        .minusDays(
                                properties.periodDays()
                        ),
                properties.purchaseWeight(),
                properties.wishlistWeight(),
                properties.viewWeight(),
                pageable
        );
    }*/

    @Transactional(readOnly = true)
    public Page<Product> getTrending(
            Pageable pageable
    ) {

        List<Long> ids =
                redisTrendingService.getTrendingIds(
                        pageable
                );

        List<Product> products =
                productService.findAllByIds(ids);

        Set<Long> existingIds =
                products.stream()
                        .map(Product::getId)
                        .collect(Collectors.toSet());


        ids.stream()
                .filter(id -> !existingIds.contains(id))
                .forEach(redisTrendingService::removeProduct);

        return new PageImpl<>(
                products,
                pageable,
                redisTrendingService.count()
        );
    }
}