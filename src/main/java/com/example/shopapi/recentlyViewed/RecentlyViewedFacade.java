package com.example.shopapi.recentlyViewed;

import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.mappers.ProductMapper;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentlyViewedFacade {

    private final RecentlyViewedService recentlyViewedService;
    private final CurrentUserService currentUserService;
    private final ProductMapper productMapper;

    public void addView(
            Long productId
    ) {
        recentlyViewedService.addView(
                currentUserService.getCurrentUserEntity(),
                productId
        );
    }

/*    public Page<ProductListResponse> getViewed(
            Pageable pageable
    ) {
        return recentlyViewedService
                .getViewed(
                        currentUserService.getCurrentUserEntity(),
                        pageable
                )
                .map(RecentlyViewedProduct::getProduct)
                .map(productMapper::toListResponse);
    }*/

    public List<ProductListResponse> getViewed() {

        return recentlyViewedService
                .getRedisViewed(
                        currentUserService.getCurrentUserEntity()
                )
                .stream()
                .map(productMapper::toListResponse)
                .toList();
    }
}
