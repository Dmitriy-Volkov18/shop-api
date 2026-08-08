package com.example.shopapi.wishlist;

import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.mappers.ProductMapper;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistFacade {

    private final WishlistService wishlistService;
    private final CurrentUserService currentUserService;
    private final ProductMapper productMapper;

    public void add(
            Long productId
    ) {
        wishlistService.add(
                currentUserService.getCurrentUserEntity(),
                productId
        );
    }

    public void remove(
            Long productId
    ) {
        wishlistService.remove(
                currentUserService.getCurrentUserEntity(),
                productId
        );
    }

    public Page<ProductListResponse> getWishlist(
            Pageable pageable
    ) {
        return wishlistService.getWishlist(
                        currentUserService.getCurrentUserEntity(),
                        pageable
                )
                .map(WishlistItem::getProduct)
                .map(productMapper::toListResponse);
    }

    public boolean contains(
            Long productId
    ) {
        return wishlistService.contains(
                currentUserService.getCurrentUserEntity(),
                productId
        );
    }
}