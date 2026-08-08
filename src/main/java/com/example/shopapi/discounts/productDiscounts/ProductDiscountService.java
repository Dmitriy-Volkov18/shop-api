package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.dto.CreateDiscountRequest;
import com.example.shopapi.discounts.dto.UpdateDiscountRequest;
import com.example.shopapi.product.cache.ProductCacheService;
import com.example.shopapi.product.cache.ProductListCacheService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.services.DiscountManagementService;
import com.example.shopapi.discounts.services.DiscountValidationService;
import com.example.shopapi.product.services.ProductPricingService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductDiscountService {
    private final ProductDiscountRepository repository;
    private final ProductDiscountMapper mapper;
    private final DiscountManagementService managementService;
    private final ProductPricingService pricingService;
    private final DiscountValidationService validationService;
    private final ProductListCacheService productListCacheService;
    private final ProductCacheService productCacheService;

    @Transactional(readOnly = true)
    public List<ProductDiscount> getDiscounts(
            ProductVariant variant
    ) {
        return repository.findByVariantId(
                variant.getId()
        );
    }

    @Transactional(readOnly = true)
    public ProductDiscount getDiscount(
            ProductVariant variant,
            Long discountId
    ) {
        return repository
                .findByIdAndVariantId(
                        discountId,
                        variant.getId()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Discount not found"
                        )
                );
    }

    public ProductDiscount create(
            ProductVariant variant,
            CreateDiscountRequest request,
            User user
    ) {

        validationService.validateDiscount(
                request.type(),
                request.discountValue(),
                pricingService.getBasePrice(variant),
                request.startsAt(),
                request.endsAt()
        );

        validationService.validateNoOverlap(
                variant,
                request.startsAt(),
                request.endsAt(),
                null
        );

        ProductDiscount discount =
                mapper.toEntity(request);

        managementService.create(
                discount,
                variant,
                user
        );

        productCacheService.evict(
                discount.getVariant().getProduct().getId()
        );

        productListCacheService.evictAll();

        return repository.save(discount);
    }

    public ProductDiscount update(
            ProductDiscount discount,
            UpdateDiscountRequest request,
            User user
    ) {

        ProductVariant variant =
                discount.getVariant();

        validationService.validateDiscount(
                request.type(),
                request.discountValue(),
                pricingService.getBasePrice(variant),
                request.startsAt(),
                request.endsAt()
        );

        validationService.validateNoOverlap(
                variant,
                request.startsAt(),
                request.endsAt(),
                discount.getId()
        );

        managementService.update(
                discount,
                user,
                () -> mapper.updateEntity(
                        request,
                        discount
                )
        );

        Long productId =
                discount.getVariant()
                        .getProduct()
                        .getId();


        productCacheService.evict(productId);
        productListCacheService.evictAll();

        return repository.save(discount);
    }

    public void delete(
            ProductDiscount discount,
            User user
    ) {
        Long productId =
                discount.getVariant()
                        .getProduct()
                        .getId();

        managementService.delete(
                discount,
                user
        );

        repository.delete(
                discount
        );

        productCacheService.evict(productId);

        productListCacheService.evictAll();
    }
}