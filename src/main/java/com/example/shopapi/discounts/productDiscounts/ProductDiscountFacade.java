package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.dto.CreateDiscountRequest;
import com.example.shopapi.discounts.productDiscounts.dto.ProductDiscountResponse;
import com.example.shopapi.discounts.dto.UpdateDiscountRequest;
import com.example.shopapi.productVariant.services.ProductVariantService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDiscountFacade {
    private final ProductVariantService productVariantService;
    private final ProductDiscountService discountService;
    private final ProductDiscountMapper discountMapper;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<ProductDiscountResponse> getDiscounts(
            Long productId,
            Long variantId
    ) {
        ProductVariant variant =
                getVariant(
                        productId,
                        variantId
                );

        authorizationService.requireProductAccess(
                variant.getProduct()
        );

        return discountService.getDiscounts(
                        variant
                )
                .stream()
                .map(discountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDiscountResponse getDiscount(
            Long productId,
            Long variantId,
            Long discountId
    ) {
        ProductVariant variant =
                getVariant(
                        productId,
                        variantId
                );

        authorizationService.requireProductAccess(
                variant.getProduct()
        );

        ProductDiscount discount =
                discountService.getDiscount(
                        variant,
                        discountId
                );

        return discountMapper.toResponse(
                discount
        );
    }

    public ProductDiscountResponse createDiscount(
            Long productId,
            Long variantId,
            CreateDiscountRequest request
    ) {
        ProductVariant variant =
                getVariant(
                        productId,
                        variantId
                );

        authorizationService.requireProductAccess(
                variant.getProduct()
        );

        User user =
                currentUserService.getCurrentUserEntity();


        ProductDiscount discount =
                discountService.create(
                        variant,
                        request,
                        user
                );

        return discountMapper.toResponse(
                discount
        );
    }

    public ProductDiscountResponse updateDiscount(
            Long productId,
            Long variantId,
            Long discountId,
            UpdateDiscountRequest request
    ) {
        ProductVariant variant =
                getVariant(
                        productId,
                        variantId
                );

        authorizationService.requireProductAccess(
                variant.getProduct()
        );

        ProductDiscount discount =
                discountService.getDiscount(
                        variant,
                        discountId
                );

        ProductDiscount updated = discountService.update(
                discount,
                request,
                currentUserService.getCurrentUserEntity()
        );

        return discountMapper.toResponse(
                updated
        );
    }

    public void deleteDiscount(
            Long productId,
            Long variantId,
            Long discountId
    ) {
        ProductVariant variant =
                getVariant(
                        productId,
                        variantId
                );

        authorizationService.requireProductAccess(
                variant.getProduct()
        );

        ProductDiscount discount =
                discountService.getDiscount(
                        variant,
                        discountId
                );

        discountService.delete(
                discount,
                currentUserService.getCurrentUserEntity()
        );
    }

    private ProductVariant getVariant(
            Long productId,
            Long variantId
    ) {
        return productVariantService.getVariant(
                productId,
                variantId
        );
    }
}