package com.example.shopapi.wishlist;

import com.example.shopapi.product.services.ProductEventService;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.product.enums.ProductEventType;
import com.example.shopapi.common.exception.conflictExceptions.WishlistItemAlreadyExistsException;
import com.example.shopapi.common.exception.notFoundExceptions.WishlistItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final ProductEventService productEventService;

    public void add(
            User user,
            Long productId
    ) {
        if (wishlistRepository.existsByUserIdAndProductId(
                user.getId(),
                productId
        )) {
            throw new WishlistItemAlreadyExistsException(
                    productId
            );
        }

        Product product = productService.getProduct(productId);

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);

        product.increaseWishlist();

        productEventService.record(
                product,
                ProductEventType.WISHLIST
        );

        wishlistRepository.save(item);
    }

    public void remove(
            User user,
            Long productId
    ) {
        WishlistItem item = getItem(user.getId(), productId);
        item.getProduct().decreaseWishlist();
        wishlistRepository.delete(item);
    }

    private WishlistItem getItem(
            Long userId,
            Long productId
    ){
        return wishlistRepository.findByUserIdAndProductId(
                        userId,
                        productId
                )
                .orElseThrow(() -> new WishlistItemNotFoundException(productId));
    }


    @Transactional(readOnly = true)
    public Page<WishlistItem> getWishlist(
            User user,
            Pageable pageable
    ) {
        return wishlistRepository.findByUserId(
                user.getId(),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public boolean contains(
            User user,
            Long productId
    ) {
        return wishlistRepository.existsByUserIdAndProductId(
                user.getId(),
                productId
        );
    }

    @Transactional(readOnly = true)
    public Set<Long> getWishlistProductIds(
            User user,
            Collection<Long> productIds
    ) {
        if (productIds.isEmpty()) {
            return Collections.emptySet();
        }

        return wishlistRepository.findProductIdsInWishlist(
                user.getId(),
                productIds
        );
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> getItems(
            User user
    ) {
        return wishlistRepository.findByUserId(
                user.getId()
        );
    }
}