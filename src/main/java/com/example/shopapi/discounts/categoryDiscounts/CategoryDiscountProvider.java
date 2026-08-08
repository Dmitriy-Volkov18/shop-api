package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.enums.DiscountSource;
import com.example.shopapi.discounts.interfaces.DiscountProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDiscountProvider
        implements DiscountProvider {

    private final CategoryDiscountRepository repository;

    @Override
    public Optional<DiscountResult> findDiscount(
            ProductVariant variant
    ) {
        Product product = variant.getProduct();

        if (product.getCategory() == null) {
            return Optional.empty();
        }

        return repository.findActiveDiscounts(
                        product.getCategory().getId(),
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .map(this::toResult);
    }

    private DiscountResult toResult(
            CategoryDiscount discount
    ) {
        return new DiscountResult(
                discount,
                DiscountSource.CATEGORY
        );
    }
}