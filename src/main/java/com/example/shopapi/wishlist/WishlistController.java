package com.example.shopapi.wishlist;

import com.example.shopapi.product.dto.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistFacade wishlistFacade;

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(
            @PathVariable Long productId
    ) {
        wishlistFacade.add(productId);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @PathVariable Long productId
    ) {
        wishlistFacade.remove(productId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<ProductListResponse> getWishlist(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return wishlistFacade.getWishlist(pageable);
    }

    @GetMapping("/{productId}/contains")
    @PreAuthorize("hasRole('USER')")
    public boolean contains(
            @PathVariable Long productId
    ) {
        return wishlistFacade.contains(productId);
    }
}