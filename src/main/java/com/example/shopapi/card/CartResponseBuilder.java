package com.example.shopapi.card;

import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.card.dto.CartItemResponse;
import com.example.shopapi.card.dto.CartResponse;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductImageService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.promotion.calculation.CartItemPrice;
import com.example.shopapi.promotion.calculation.CartItemPricingService;
import com.example.shopapi.promotion.calculation.CartPriceResult;
import com.example.shopapi.promotion.calculation.CartPricingService;
import com.example.shopapi.promotion.context.PromotionContextBuilder;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartResponseBuilder {

    private final CurrentUserService currentUserService;
    private final PromotionContextBuilder contextBuilder;
    private final CartPricingService cartPricingService;
    private final CartItemPricingService cartItemPricingService;
    private final ProductImageService productImageService;

    public CartResponse build(Cart cart) {
        User user = currentUserService.getCurrentUserEntity();

        PromotionContext context =
                contextBuilder.build(
                        user,
                        cart,
                        null,
                        null
                );

        CartPriceResult priceResult =
                cartPricingService.calculate(
                        cart,
                        context
                );

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(item ->
                                buildItemResponse(
                                        item,
                                        priceResult,
                                        cart
                                )
                        )
                        .toList();

        int totalItems =
                items.stream()
                        .mapToInt(CartItemResponse::quantity)
                        .sum();

        return new CartResponse(
                items,
                totalItems,
                priceResult.total()
        );
    }

    private CartItemResponse buildItemResponse(
            CartItem item,
            CartPriceResult priceResult,
            Cart cart
    ) {
        ProductVariant variant = item.getVariant();

        CartItemPrice price =
                cartItemPricingService.calculate(
                        item,
                        priceResult,
                        cart
                );

        return new CartItemResponse(
                item.getId(),
                variant.getId(),
                variant.getProduct().getName(),
                variant.getSku(),
                productImageService.getMainImage(
                        variant
                ),
                item.getQuantity(),
                price.originalUnitPrice(),
                price.discountAmount(),
                price.finalUnitPrice(),
                price.subtotal()
        );
    }
}