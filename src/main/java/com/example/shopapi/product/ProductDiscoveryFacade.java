package com.example.shopapi.product;

import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.services.ProductRecommendationService;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.product.services.RecommendationService;
import com.example.shopapi.product.services.TrendingProductService;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDiscoveryFacade {

    private final ProductService productService;
    private final ProductRecommendationService productRecommendationService;
    private final ProductResponseAssembler productResponseAssembler;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;
    private final RecommendationService recommendationService;
    private final TrendingProductService trendingProductService;

    public Page<ProductListResponse> getSimilarProducts(
            Long id,
            Pageable pageable
    ) {
        Product product = productService.getProduct(id);

        Page<Product> page =
                productRecommendationService.findSimilarProducts(
                        product,
                        pageable
                );

        return productResponseAssembler.toPage(page, pageable, getCurrentUserOrNull());
    }

    private User getCurrentUserOrNull() {
        return authorizationService.isAuthenticated()
                ? currentUserService.getCurrentUserEntity()
                : null;
    }

    public Page<ProductListResponse> getAlsoBought(
            Long id,
            Pageable pageable
    ) {
        Product product = productService.getProduct(id);

        Page<Product> products =
                productRecommendationService.findAlsoBought(
                        product,
                        pageable
                );


        return productResponseAssembler.toPage(products, pageable, getCurrentUserOrNull());
    }

    public Page<ProductListResponse> recommend(
            Pageable pageable
    ){
        User user = currentUserService.getCurrentUserEntity();

        Page<Product> page =
                recommendationService.recommend(
                        user,
                        pageable
                );

        return productResponseAssembler.toPage(page, pageable, getCurrentUserOrNull());
    }

    public Page<ProductListResponse> getTrending(
            Pageable pageable
    ) {
        Page<Product> page =
                trendingProductService.getTrending(
                        pageable
                );

        return productResponseAssembler.toPage(page, pageable, getCurrentUserOrNull());
    }
}
