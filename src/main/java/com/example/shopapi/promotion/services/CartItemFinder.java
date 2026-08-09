package com.example.shopapi.promotion.services;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductBasePriceService;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemFinder {

    private final ProductBasePriceService basePriceService;

    public List<CartItem> findItems(
            Cart cart,
            PromotionTargetType targetType,
            Long targetId
    ) {

        return switch (targetType) {

            case ALL ->
                    cart.getItems();

            case PRODUCT ->
                    findProductItems(
                            cart,
                            targetId
                    );

            case CATEGORY ->
                    findCategoryItems(
                            cart,
                            targetId
                    );

            case BRAND ->
                    findBrandItems(
                            cart,
                            targetId
                    );

            case SAME_PRODUCT ->
                    throw new IllegalArgumentException(
                            "SAME_PRODUCT must be resolved separately."
                    );
        };
    }

    private List<CartItem> findProductItems(
            Cart cart,
            Long productId
    ) {
        return cart.getItems()
                .stream()
                .filter(item ->
                        item.getVariant()
                                .getProduct()
                                .getId()
                                .equals(productId)
                )
                .toList();
    }

    private List<CartItem> findCategoryItems(
            Cart cart,
            Long categoryId
    ) {
        return cart.getItems()
                .stream()
                .filter(item ->
                        item.getVariant()
                                .getProduct()
                                .getCategory()
                                .getId()
                                .equals(categoryId)
                )
                .toList();
    }

    private List<CartItem> findBrandItems(
            Cart cart,
            Long brandId
    ) {
        return cart.getItems()
                .stream()
                .filter(item ->
                        item.getVariant()
                                .getProduct()
                                .getBrand()
                                .getId()
                                .equals(brandId)
                )
                .toList();
    }

    public Optional<CartItem> findCheapestItem(
            Cart cart,
            PromotionTargetType targetType,
            Long targetId
    ){
        return findItems(cart, targetType, targetId)
                .stream()
                .min(
                        Comparator.comparing(
                                item ->
                                        basePriceService.getBasePrice(
                                                item.getVariant()
                                        )
                        )
                );
    }

}
