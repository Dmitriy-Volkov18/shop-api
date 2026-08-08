package com.example.shopapi.productVariant.services;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.dto.ProductVariantResponse;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.mappers.DiscountResultMapper;
import com.example.shopapi.productVariant.mappers.ProductVariantMapper;
import com.example.shopapi.discounts.DiscountResolver;
import com.example.shopapi.product.services.ProductPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductVariantResponseService {

    private final ProductVariantMapper mapper;
    private final ProductPricingService productPricingService;
    private final DiscountResultMapper discountResultMapper;
    private final DiscountResolver discountResolver;

    public ProductVariantResponse toResponse(
            ProductVariant variant
    ) {
        ProductVariantResponse response =
                mapper.toResponse(variant);

        Optional<DiscountResult> activeDiscount =
                discountResolver.resolve(
                        variant
                );

        return new ProductVariantResponse(
                response.id(),
                response.sku(),
                response.price(),
                productPricingService.calculateEffectivePrice(
                        variant
                ),
                activeDiscount
                        .map(discountResultMapper::toResponse)
                        .orElse(null),

                response.availableQuantity(),
                response.status(),
                response.dimensions(),
                response.attributes(),
                response.images()
        );
    }
}