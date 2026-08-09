package com.example.shopapi.product;

import com.example.shopapi.product.cache.ProductCacheKeyGenerator;
import com.example.shopapi.product.cache.ProductCacheService;
import com.example.shopapi.product.cache.ProductListCacheService;
import com.example.shopapi.product.dto.CreateProductRequest;
import com.example.shopapi.product.dto.ProductDetailResponse;
import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.dto.ProductListResponsePage;
import com.example.shopapi.product.dto.UpdateProductRequest;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.product.mappers.ProductMapper;
import com.example.shopapi.recentlyViewed.RecentlyViewedService;
import com.example.shopapi.searchHistory.SearchHistoryService;
import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.wishlist.WishlistService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;
    private final WishlistService wishlistService;
    private final RecentlyViewedService recentlyViewedService;
    private final SearchHistoryService searchHistoryService;
    private final ProductResponseAssembler productResponseAssembler;
    private final ProductCacheService productCacheService;
    private final ProductListCacheService productListCacheService;
    private final ProductCacheKeyGenerator productCacheKeyGenerator;

    @Transactional
    public ProductDetailResponse createProduct(
            CreateProductRequest request,
            List<MultipartFile> files
    ) {
        User user = currentUserService.getCurrentUserEntity();

        Product product =
                productService.createProduct(
                        user,
                        request,
                        files
                );

        productListCacheService.evictAll();

        return productMapper.toDetailResponse(product);
    }

    public ProductDetailResponse getProduct(
            Long id
    ) {
        ProductDetailResponse cached = productCacheService.get(id);

        if (cached != null) {
            recordView(id);

            return cached;
        }

        Product product = productService.getProduct(id);
        recordView(id);

        ProductDetailResponse response = productMapper.toDetailResponse(product);

        productCacheService.put(response);

        return response;
    }

    private void recordView(Long productId) {
        if (!authorizationService.isAuthenticated()) {
            return;
        }

        recentlyViewedService.addView(
                currentUserService.getCurrentUserEntity(),
                productId
        );
    }

    public Page<ProductListResponse> getProducts(
            ProductFilter filter,
            Pageable pageable
    ) {
        if (authorizationService.isAuthenticated()) {
            searchHistoryService.save(
                    currentUserService.getCurrentUserEntity(),
                    filter.getSearch()
            );
        }

        String cacheKey =
                productCacheKeyGenerator.generate(
                        filter,
                        pageable
                );


        ProductListResponsePage cached =
                productListCacheService.get(
                        cacheKey
                );

        if (cached != null) {
            return new PageImpl<>(
                    cached.content(),
                    pageable,
                    cached.totalElements()
            );
        }

        Page<Product> page =
                productService.getProducts(
                        filter,
                        pageable
                );

        User user = authorizationService.isAuthenticated()
                ? currentUserService.getCurrentUserEntity()
                : null;

        Page<ProductListResponse> response =
                productResponseAssembler.toPage(
                        page,
                        pageable,
                        user
                );

        ProductListResponsePage cachePage =
                new ProductListResponsePage(
                        response.getContent(),
                        response.getNumber(),
                        response.getSize(),
                        response.getTotalElements(),
                        response.getTotalPages()
                );

        productListCacheService.put(
                cacheKey,
                cachePage
        );

        return response;
    }

    @Transactional
    public ProductDetailResponse updateProduct(
            Long id,
            UpdateProductRequest request,
            List<MultipartFile> files
    ) {
        Product product = productService.getProduct(id);

        authorizationService.requireProductAccess(product);

        Product updated =
                productService.updateProduct(
                        product,
                        request,
                        files
                );

        productCacheService.evict(updated.getId());
        productListCacheService.evictAll();

        return productMapper.toDetailResponse(updated);
    }

    public void deactivate(Long id) {
        Product product = productService.getProduct(id);

        authorizationService.requireProductAccess(product);
        productService.deactivate(product);
        productCacheService.evict(product.getId());
        productListCacheService.evictAll();
    }

    public ProductDetailResponse updateStock(
            Long id,
            int quantity
    ) {
        Product product = productService.getProduct(id);

        authorizationService.requireProductAccess(product);

        productService.increaseStock(
                product,
                quantity
        );

        productCacheService.evict(product.getId());
        productListCacheService.evictAll();

        return productMapper.toDetailResponse(product);
    }

}