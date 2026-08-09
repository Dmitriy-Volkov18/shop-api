package com.example.shopapi.promotion.engine;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PromotionContext {

    private final User user;
    private final Cart cart;
    private final CustomerOrder order;
    private final ProductVariant variant;
    private final LocalDateTime now;
    private final BigDecimal cartTotal;

}