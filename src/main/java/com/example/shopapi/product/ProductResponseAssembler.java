package com.example.shopapi.product;

import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.mappers.ProductMapper;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductResponseAssembler {

    private final ProductMapper mapper;
    private final WishlistService wishlistService;

    public Page<ProductListResponse> toPage(
            Page<Product> page,
            Pageable pageable,
            User user
    ){
        Set<Long> favoriteIds =
                resolveFavorites(
                        page.getContent(),
                        user
                );

        return page.map(product -> {
            ProductListResponse response = mapper.toListResponse(product);

            return new ProductListResponse(
                    response.id(),
                    response.name(),
                    response.price(),
                    response.brand(),
                    response.category(),
                    response.seller(),
                    response.status(),
                    response.mainImage(),
                    response.averageRating(),
                    response.reviewCount(),
                    favoriteIds.contains(
                            product.getId()
                    )
            );

        });
    }

    private Set<Long> resolveFavorites(
            List<Product> products,
            User user
    ){
        if(user == null){
            return Collections.emptySet();
        }

        return wishlistService.getWishlistProductIds(
                user,
                products.stream()
                        .map(Product::getId)
                        .collect(Collectors.toSet())
        );
    }
}