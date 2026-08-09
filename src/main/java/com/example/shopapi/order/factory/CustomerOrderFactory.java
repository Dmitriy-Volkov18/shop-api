package com.example.shopapi.order.factory;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.common.config.ShopProperties;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.product.services.ProductImageService;
import com.example.shopapi.promotion.calculation.CartItemPrice;
import com.example.shopapi.promotion.calculation.CartItemPricingService;
import com.example.shopapi.promotion.calculation.CartPriceResult;
import com.example.shopapi.promotion.calculation.CartPricingService;
import com.example.shopapi.promotion.context.PromotionContextBuilder;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomerOrderFactory {

    private final CartPricingService cartPricingService;
    private final ProductImageService productImageService;
    private final PromotionContextBuilder contextBuilder;
    private final CartItemPricingService cartItemPricingService;
    private final ShopProperties properties;

    public CustomerOrder create(
            User user,
            Cart cart
    ) {
        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setStatus(CustomerOrderStatus.PENDING);
        order.setPaymentExpiresAt(
                LocalDateTime.now()
                        .plus(
                                properties
                                        .getOrder()
                                        .getPaymentTimeout()
                        )
        );

        PromotionContext context =
                contextBuilder.build(
                        user,
                        cart,
                        order,
                        null
                );

        CartPriceResult priceResult =
                cartPricingService.calculate(
                        cart,
                        context
                );

        for (CartItem cartItem : cart.getItems()) {
            CartItemPrice itemPrice =
                    cartItemPricingService.calculate(
                            cartItem,
                            priceResult,
                            cart
                    );

            order.addItem(
                    createItem(
                            cartItem,
                            itemPrice
                    )
            );
        }

        order.calculateTotals();

        return order;
    }

    private CustomerOrderItem createItem(
            CartItem cartItem,
            CartItemPrice price
    ){
        ProductVariant variant = cartItem.getVariant();

        CustomerOrderItem item = new CustomerOrderItem();
        item.setVariant(variant);
        item.setProductName(variant.getProduct().getName());
        item.setSku(variant.getSku());
        item.setImageUrl(productImageService.getMainImage(variant));
        item.setQuantity(cartItem.getQuantity());
        item.setUnitPrice(price.finalUnitPrice());
        item.calculateTotalPrice();

        return item;
    }
}